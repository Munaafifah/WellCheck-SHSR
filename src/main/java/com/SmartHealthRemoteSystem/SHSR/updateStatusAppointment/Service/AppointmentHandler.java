package com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service;

import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Model.Appointment;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.mongodb.client.result.DeleteResult;

@Service
public class AppointmentHandler {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentHandler.class);
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@atlascluster.htlbqbu.mongodb.net/?retryWrites=true&w=majority&appName=AtlasCluster";

    @Autowired
    private EmailService emailService;

    // ── Helper: read costItems array from a MongoDB document ─────────────────
    private List<Map<String, Object>> extractCostItems(Document doc) {
        List<Map<String, Object>> costItems = new ArrayList<>();
        Object raw = doc.get("costItems");
        if (raw instanceof List) {
            for (Object item : (List<?>) raw) {
                if (item instanceof Document) {
                    Document d = (Document) item;
                    Map<String, Object> map = new HashMap<>();
                    map.put("label", d.getString("label"));
                    Object amt = d.get("amount");
                    map.put("amount", amt instanceof Number ? ((Number) amt).doubleValue() : 0.0);
                    costItems.add(map);
                }
            }
        }
        return costItems;
    }

    // ── fetch all appointments ────────────────────────────────────────────────
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        MongoClient mongoClient = null;

        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            FindIterable<Document> results = collection.find();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Document doc : results) {
                String appointmentDate = null;
                boolean isExpired = false;

                Object dateObj = doc.get("appointmentDate");
                try {
                    if (dateObj instanceof Date) {
                        appointmentDate = ((Date) dateObj)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString();
                    } else if (dateObj instanceof String) {
                        appointmentDate = (String) dateObj;
                    }

                    if (appointmentDate != null) {
                        LocalDate appDate = LocalDate.parse(appointmentDate, formatter);
                        isExpired = appDate.isBefore(today);
                        logger.info("Appointment date: {}, Today: {}, Is expired: {}", appDate, today, isExpired);
                    }
                } catch (DateTimeParseException e) {
                    logger.error("Error parsing date: {} - {}", appointmentDate, e.getMessage());
                    continue;
                }

                String timestampStr = null;
                Object timestampObj = doc.get("timestamp");
                if (timestampObj instanceof Date) {
                    timestampStr = ((Date) timestampObj).toInstant().toString();
                } else if (timestampObj instanceof Document) {
                    Date embeddedDate = ((Document) timestampObj).getDate("$date");
                    if (embeddedDate != null) {
                        timestampStr = embeddedDate.toInstant().toString();
                    }
                }

                String status = doc.getString("statusAppointment");
                if (isExpired) {
                    status = "Expired";
                    if (!"Expired".equals(doc.getString("statusAppointment"))) {
                        Document filter = new Document("appointmentId", doc.getString("appointmentId"));
                        Document update = new Document("$set", new Document("statusAppointment", "Expired"));
                        collection.updateOne(filter, update);
                    }
                }

                Appointment appointment = new Appointment(
                        doc.getObjectId("_id").toString(),
                        doc.getString("appointmentId"),
                        doc.getString("userId"),
                        doc.getString("doctorId"),
                        doc.getString("hospitalId"),
                        appointmentDate,
                        doc.getString("appointmentTime"),
                        doc.getString("duration"),
                        doc.getString("registeredHospital"),
                        doc.getString("typeOfSickness"),
                        doc.getString("additionalNotes"),
                        doc.getString("insuranceProvider"),
                        doc.getString("insurancePolicyNumber"),
                        doc.getString("email"),
                        doc.getInteger("appointmentCost") != null ? doc.getInteger("appointmentCost") : 0,
                        doc.getString("statusPayment"),
                        status,
                        timestampStr);

                // ── replaced setConsultationCost/setEquipmentCost with costItems ──
                appointment.setCostItems(extractCostItems(doc));

                appointments.add(appointment);
                logger.info("Added appointment: ID={}, Date={}, Status={}",
                        appointment.getAppointmentId(),
                        appointment.getAppointmentDate(),
                        appointment.getStatusAppointment());
            }
        } catch (Exception e) {
            logger.error("Error fetching appointments: {}", e.getMessage(), e);
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
        return appointments;
    }

    public boolean isTimeSlotAvailable(String doctorId, String appointmentDate, String appointmentTime,
            String currentAppointmentId) {
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document query = new Document()
                    .append("doctorId", doctorId)
                    .append("appointmentDate", appointmentDate)
                    .append("appointmentTime", appointmentTime)
                    .append("statusAppointment", "Approved");

            if (currentAppointmentId != null && !currentAppointmentId.isEmpty()) {
                query.append("appointmentId", new Document("$ne", currentAppointmentId));
            }

            logger.info("Checking time slot with query: {}", query.toJson());

            long conflictingAppointments = collection.countDocuments(query);

            logger.info("Time slot check - Doctor: {}, Date: {}, Time: {}, Current Appt ID: {}, Conflicts: {}",
                    doctorId, appointmentDate, appointmentTime, currentAppointmentId, conflictingAppointments);

            Document currentAppointment = collection.find(new Document("appointmentId", currentAppointmentId)).first();
            if (currentAppointment != null) {
                String appointmentDoctorId = currentAppointment.getString("doctorId");
                logger.info("Current appointment's doctor ID: {}, Checking doctor ID: {}", appointmentDoctorId,
                        doctorId);

                if (!appointmentDoctorId.equals(doctorId)) {
                    logger.info("Different doctors - no conflict check needed");
                    return true;
                }
            }

            return conflictingAppointments == 0;

        } catch (Exception e) {
            logger.error("Error checking time slot availability: {}", e.getMessage(), e);
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public boolean updateAppointmentStatus(String appointmentId, String newStatus) {
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document appointment = collection.find(new Document("appointmentId", appointmentId)).first();
            if (appointment == null) {
                logger.error("Appointment not found: {}", appointmentId);
                return false;
            }

            if (newStatus.equals("Approved")) {
                String doctorId = appointment.getString("doctorId");
                String appointmentDate = appointment.getString("appointmentDate");
                String appointmentTime = appointment.getString("appointmentTime");

                if (!isTimeSlotAvailable(doctorId, appointmentDate, appointmentTime, appointmentId)) {
                    logger.warn("Time slot conflict detected for appointment: {}", appointmentId);
                    throw new RuntimeException("SLOT_CONFLICT");
                }
            }

            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document("statusAppointment", newStatus));

            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() > 0) {
                String patientEmail = appointment.getString("email");
                try {
                    emailService.sendAppointmentStatusEmail(patientEmail, appointmentId, newStatus);
                    logger.info("Status update email sent to patient: {}", patientEmail);
                } catch (Exception e) {
                    logger.error("Failed to send status update email: {}", e.getMessage());
                }
                return true;
            }
            return false;

        } catch (RuntimeException e) {
            if (e.getMessage().equals("SLOT_CONFLICT")) {
                throw e;
            }
            logger.error("Error updating appointment status: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error updating appointment status: {}", e.getMessage());
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public List<Appointment> getAppointmentsByDoctorId(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        MongoClient mongoClient = null;

        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document query = new Document("doctorId", doctorId);
            FindIterable<Document> results = collection.find(query);
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Document doc : results) {
                String appointmentDate;
                Object dateObj = doc.get("appointmentDate");
                if (dateObj instanceof Date) {
                    appointmentDate = ((Date) dateObj)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .toString();
                } else if (dateObj instanceof String) {
                    appointmentDate = (String) dateObj;
                } else {
                    logger.warn("Unexpected date format for appointmentId: {}", doc.getString("appointmentId"));
                    appointmentDate = dateObj != null ? dateObj.toString() : null;
                }

                boolean isExpired = false;
                try {
                    LocalDate appDate = LocalDate.parse(appointmentDate, formatter);
                    isExpired = appDate.isBefore(today);
                } catch (Exception e) {
                    logger.error("Error parsing date: {}", appointmentDate, e);
                }

                String timestampStr = null;
                Object timestampObj = doc.get("timestamp");
                if (timestampObj instanceof Date) {
                    timestampStr = ((Date) timestampObj).toInstant().toString();
                } else if (timestampObj instanceof Document) {
                    Date embeddedDate = ((Document) timestampObj).getDate("$date");
                    if (embeddedDate != null) {
                        timestampStr = embeddedDate.toInstant().toString();
                    }
                }

                String status = doc.getString("statusAppointment");
                if (isExpired) {
                    status = "Expired";
                }

                Appointment appointment = new Appointment(
                        doc.getObjectId("_id").toString(),
                        doc.getString("appointmentId"),
                        doc.getString("userId"),
                        doc.getString("doctorId"),
                        doc.getString("hospitalId"),
                        appointmentDate,
                        doc.getString("appointmentTime"),
                        doc.getString("duration"),
                        doc.getString("registeredHospital"),
                        doc.getString("typeOfSickness"),
                        doc.getString("additionalNotes"),
                        doc.getString("insuranceProvider"),
                        doc.getString("insurancePolicyNumber"),
                        doc.getString("email"),
                        doc.getInteger("appointmentCost") != null ? doc.getInteger("appointmentCost") : 0,
                        doc.getString("statusPayment"),
                        status,
                        timestampStr);

                // ── replaced setConsultationCost/setEquipmentCost with costItems ──
                appointment.setCostItems(extractCostItems(doc));

                appointments.add(appointment);
            }
        } catch (Exception e) {
            logger.error("Error fetching appointments: {}", e.getMessage(), e);
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
        return appointments;
    }

    public Map<String, Object> validateAndUpdateStatus(String appointmentId, String newStatus) {
        Map<String, Object> response = new HashMap<>();
        MongoClient mongoClient = null;

        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document appointmentToUpdate = collection.find(new Document("appointmentId", appointmentId)).first();

            if (appointmentToUpdate == null) {
                response.put("success", false);
                response.put("message", "Appointment not found");
                return response;
            }

            if (!"Approved".equals(newStatus)) {
                return updateAppointmentStatusInternal(collection, appointmentId, newStatus);
            }

            Document query = new Document("doctorId", appointmentToUpdate.getString("doctorId"))
                    .append("appointmentDate", appointmentToUpdate.get("appointmentDate"))
                    .append("appointmentTime", appointmentToUpdate.getString("appointmentTime"))
                    .append("statusAppointment", "Approved")
                    .append("appointmentId", new Document("$ne", appointmentId));

            long existingApprovedCount = collection.countDocuments(query);

            if (existingApprovedCount > 0) {
                response.put("success", false);
                response.put("message", "This time slot is already booked. Please choose another slot.");
                return response;
            }

            return updateAppointmentStatusInternal(collection, appointmentId, newStatus);

        } catch (Exception e) {
            logger.error("Error validating and updating appointment status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while updating the appointment status");
            return response;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    private Map<String, Object> updateAppointmentStatusInternal(MongoCollection<Document> collection,
            String appointmentId,
            String newStatus) {
        Map<String, Object> response = new HashMap<>();

        try {
            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document("statusAppointment", newStatus));
            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() > 0) {
                Document appointment = collection.find(filter).first();
                if (appointment != null) {
                    String patientEmail = appointment.getString("email");
                    try {
                        emailService.sendAppointmentStatusEmail(patientEmail, appointmentId, newStatus);
                        logger.info("Status update email sent to patient: {}", patientEmail);
                    } catch (Exception e) {
                        logger.error("Failed to send status update email: {}", e.getMessage());
                    }
                }
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "No appointment was updated");
            }
        } catch (Exception e) {
            logger.error("Error in updateAppointmentStatusInternal: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while updating the appointment status");
        }

        return response;
    }

    public Map<String, Object> validateAndUpdateDateTime(String appointmentId, String newDate, String newTime) {
        Map<String, Object> response = new HashMap<>();
        MongoClient mongoClient = null;

        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document appointmentToUpdate = collection.find(new Document("appointmentId", appointmentId)).first();

            if (appointmentToUpdate == null) {
                response.put("success", false);
                response.put("message", "Appointment not found");
                return response;
            }

            Document query = new Document("doctorId", appointmentToUpdate.getString("doctorId"))
                    .append("appointmentDate", newDate)
                    .append("appointmentTime", newTime)
                    .append("statusAppointment", "Approved")
                    .append("appointmentId", new Document("$ne", appointmentId));

            long existingAppointmentCount = collection.countDocuments(query);

            if (existingAppointmentCount > 0) {
                response.put("success", false);
                response.put("message", "This time slot is already booked. Please choose another slot.");
                return response;
            }

            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document()
                    .append("appointmentDate", newDate)
                    .append("appointmentTime", newTime));

            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() > 0) {
                Document appointment = collection.find(filter).first();
                if (appointment != null) {
                    String patientEmail = appointment.getString("email");
                    try {
                        emailService.sendAppointmentUpdateEmail(patientEmail, appointmentId, newDate, newTime);
                        logger.info("Appointment update email sent to patient: {}", patientEmail);
                    } catch (Exception e) {
                        logger.error("Failed to send appointment update email: {}", e.getMessage());
                    }
                }
                response.put("success", true);
                response.put("message", "Appointment updated successfully");
            } else {
                response.put("success", false);
                response.put("message", "No appointment was updated");
            }

        } catch (Exception e) {
            logger.error("Error updating appointment date/time: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while updating the appointment");
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }

        return response;
    }

    public Map<String, Object> getPatientContactInfo(String userId) {
        Map<String, Object> response = new HashMap<>();
        MongoClient mongoClient = null;

        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("Patient");

            Document query = new Document("_id", userId);
            Document patient = collection.find(query).first();

            if (patient != null && patient.containsKey(userId)) {
                Document patientData = (Document) patient.get(userId);
                response.put("success", true);
                response.put("contact", patientData.getString("contact"));
                response.put("emergencyContact", patientData.getString("emergencyContact"));
                response.put("name", patientData.getString("name"));
            } else {
                response.put("success", false);
                response.put("message", "Patient not found");
            }

        } catch (Exception e) {
            logger.error("Error fetching patient contact info: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching patient contact information");
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }

        return response;
    }

    // ── updateAppointmentCost — now accepts dynamic costItems list ────────────
    public Map<String, Object> updateAppointmentCost(String appointmentId, List<Map<String, Object>> costItems) {
        Map<String, Object> response = new HashMap<>();
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            // Convert List<Map> to List<Document> for MongoDB
            List<Document> costDocs = new ArrayList<>();
            for (Map<String, Object> item : costItems) {
                Document d = new Document();
                d.append("label", item.get("label").toString());
                Object amt = item.get("amount");
                d.append("amount", amt instanceof Number ? ((Number) amt).doubleValue() : 0.0);
                costDocs.add(d);
            }

            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document("costItems", costDocs));

            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() > 0) {
                response.put("success", true);
                logger.info("Cost items updated for appointment: {}", appointmentId);
            } else {
                response.put("success", false);
                response.put("message", "No appointment was updated");
            }
        } catch (Exception e) {
            logger.error("Error updating cost items: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while updating costs");
        } finally {
            if (mongoClient != null)
                mongoClient.close();
        }
        return response;
    }

    public Map<String, Object> deleteAppointment(String appointmentId) {
        Map<String, Object> response = new HashMap<>();
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document filter = new Document("appointmentId", appointmentId);
            DeleteResult result = collection.deleteOne(filter);

            if (result.getDeletedCount() > 0) {
                response.put("success", true);
                logger.info("Appointment deleted: {}", appointmentId);
            } else {
                response.put("success", false);
                response.put("message", "No appointment found to delete");
            }
        } catch (Exception e) {
            logger.error("Error deleting appointment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while deleting appointment");
        } finally {
            if (mongoClient != null)
                mongoClient.close();
        }
        return response;
    }

    public Map<String, Object> updateDrugCost(String appointmentId, double drugCost) {
        Map<String, Object> response = new HashMap<>();
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");

            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document("drugCost", drugCost));
            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() > 0) {
                response.put("success", true);
                logger.info("Drug cost updated for appointment: {}", appointmentId);
            } else {
                response.put("success", false);
                response.put("message", "No appointment found");
            }
        } catch (Exception e) {
            logger.error("Error updating drug cost: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating drug cost");
        } finally {
            if (mongoClient != null)
                mongoClient.close();
        }
        return response;
    }
}