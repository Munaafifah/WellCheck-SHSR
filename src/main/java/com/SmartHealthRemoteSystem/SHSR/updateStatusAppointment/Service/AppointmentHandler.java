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

@Service
public class AppointmentHandler {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentHandler.class);
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@atlascluster.htlbqbu.mongodb.net/?retryWrites=true&w=majority&appName=AtlasCluster";

    @Autowired
    private EmailService emailService;

    //fetch all appointments
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
                
                // Handle appointment date
                Object dateObj = doc.get("appointmentDate");
                try {
                    if (dateObj instanceof Date) {
                        appointmentDate = ((Date) dateObj)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            // .format(formatter);
                            .toString();
                    } else if (dateObj instanceof String) {
                        appointmentDate = (String) dateObj;
                    }
                    
                    // Check if appointment is expired
                    if (appointmentDate != null) {
                        LocalDate appDate = LocalDate.parse(appointmentDate, formatter);
                        isExpired = appDate.isBefore(today);
                        logger.info("Appointment date: {}, Today: {}, Is expired: {}", appDate, today, isExpired);
                    }
                } catch (DateTimeParseException e) {
                    logger.error("Error parsing date: {} - {}", appointmentDate, e.getMessage());
                    continue; // Skip this appointment if date parsing fails
                }
    
                // Handle timestamp
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
    
                // Set status
                String status = doc.getString("statusAppointment");
                if (isExpired) {
                    status = "Expired";
                    // Update the status in the database if it's not already marked as expired
                    if (!doc.getString("statusAppointment").equals("Expired")) {
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
                    doc.getInteger("appointmentCost"),
                    doc.getString("statusPayment"),
                    status,
                    timestampStr
                );
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

    public boolean isTimeSlotAvailable(String doctorId, String appointmentDate, String appointmentTime, String currentAppointmentId) {
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
            MongoCollection<Document> collection = database.getCollection("appointments");
    
            // Build query to check for conflicting appointments
            Document query = new Document()
                .append("doctorId", doctorId)
                .append("appointmentDate", appointmentDate)
                .append("appointmentTime", appointmentTime)
                .append("statusAppointment", "Approved");
    
            // If updating an existing appointment, exclude it from the check
            if (currentAppointmentId != null && !currentAppointmentId.isEmpty()) {
                query.append("appointmentId", new Document("$ne", currentAppointmentId));
            }
    
            // Log the query for debugging
            logger.info("Checking time slot with query: {}", query.toJson());
    
            // Count conflicting appointments
            long conflictingAppointments = collection.countDocuments(query);
            
            logger.info("Time slot check - Doctor: {}, Date: {}, Time: {}, Current Appt ID: {}, Conflicts: {}", 
                doctorId, appointmentDate, appointmentTime, currentAppointmentId, conflictingAppointments);
    
            // Get the appointment details for additional validation
            Document currentAppointment = collection.find(new Document("appointmentId", currentAppointmentId)).first();
            if (currentAppointment != null) {
                String appointmentDoctorId = currentAppointment.getString("doctorId");
                logger.info("Current appointment's doctor ID: {}, Checking doctor ID: {}", appointmentDoctorId, doctorId);
                
                // Only check for conflicts if it's the same doctor
                if (!appointmentDoctorId.equals(doctorId)) {
                    logger.info("Different doctors - no conflict check needed");
                    return true;
                }
            }
            
            // The slot is available if there are no conflicting appointments
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
    
            // First, get the appointment details
            Document appointment = collection.find(new Document("appointmentId", appointmentId)).first();
            if (appointment == null) {
                logger.error("Appointment not found: {}", appointmentId);
                return false;
            }
    
            // Only check for conflicts if trying to approve the appointment
            if (newStatus.equals("Approved")) {
                String doctorId = appointment.getString("doctorId");
                String appointmentDate = appointment.getString("appointmentDate");
                String appointmentTime = appointment.getString("appointmentTime");
    
                if (!isTimeSlotAvailable(doctorId, appointmentDate, appointmentTime, appointmentId)) {
                    logger.warn("Time slot conflict detected for appointment: {}", appointmentId);
                    throw new RuntimeException("SLOT_CONFLICT");
                }
            }
    
            // Proceed with the update if no conflicts or if not approving
            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document("statusAppointment", newStatus));
            
            UpdateResult result = collection.updateOne(filter, update);
    
            if (result.getModifiedCount() > 0) {
                // Send email notification
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
                throw e; // Re-throw for specific handling
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
    
                // Check if appointment is expired
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
                    doc.getInteger("appointmentCost"),
                    doc.getString("statusPayment"),
                    status,
                    timestampStr
                );
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

            // First get the appointment we want to update
            Document appointmentToUpdate = collection.find(new Document("appointmentId", appointmentId)).first();
            
            if (appointmentToUpdate == null) {
                response.put("success", false);
                response.put("message", "Appointment not found");
                return response;
            }

            // If we're not approving, just update normally
            if (!"Approved".equals(newStatus)) {
                return updateAppointmentStatusInternal(collection, appointmentId, newStatus);
            }

            // Check for existing approved appointments in the same slot
            Document query = new Document("doctorId", appointmentToUpdate.getString("doctorId"))
                .append("appointmentDate", appointmentToUpdate.get("appointmentDate"))
                .append("appointmentTime", appointmentToUpdate.getString("appointmentTime"))
                .append("statusAppointment", "Approved")
                .append("appointmentId", new Document("$ne", appointmentId)); // Exclude current appointment

            long existingApprovedCount = collection.countDocuments(query);

            if (existingApprovedCount > 0) {
                response.put("success", false);
                response.put("message", "This time slot is already booked. Please choose another slot.");
                return response;
            }

            // If no conflicts, proceed with the update
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
                // Get updated appointment for email notification
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

            // Get the appointment we want to update
            Document appointmentToUpdate = collection.find(new Document("appointmentId", appointmentId)).first();
            
            if (appointmentToUpdate == null) {
                response.put("success", false);
                response.put("message", "Appointment not found");
                return response;
            }

            // Check for existing appointments in the new time slot
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

            // Update appointment date and time
            Document filter = new Document("appointmentId", appointmentId);
            Document update = new Document("$set", new Document()
                .append("appointmentDate", newDate)
                .append("appointmentTime", newTime));
            
            UpdateResult result = collection.updateOne(filter, update);

            if (result.getModifiedCount() > 0) {
                // Get updated appointment for email notification
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
                response.put("name", patientData.getString("name")); // Including name for reference
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

        // public List<Appointment> getAppointmentsByDoctorId(String doctorId) {
    //     List<Appointment> appointments = new ArrayList<>();
    //     MongoClient mongoClient = null;
    //     logger.info(doctorId + ": AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    
    //     try {
    //         logger.info("Connecting to MongoDB...");
    //         mongoClient = MongoClients.create(CONNECTION_STRING);
    //         MongoDatabase database = mongoClient.getDatabase("Wellcheck2");
    //         MongoCollection<Document> collection = database.getCollection("appointments");
            
    //         logger.info("Fetching appointments for doctorId: {}", doctorId);
    //         Document query = new Document("doctorId", doctorId);
    //         FindIterable<Document> results = collection.find(query);
    
    //         for (Document doc : results) {
    //             logger.info("Processing document: {}", doc.toJson());
                
    //             // Safely handle the appointment date
    //             String appointmentDate;
    //             Object dateObj = doc.get("appointmentDate");
    //             if (dateObj instanceof Date) {
    //                 appointmentDate = ((Date) dateObj)
    //                     .toInstant()
    //                     .atZone(ZoneId.systemDefault())
    //                     .toLocalDate()
    //                     .toString();
    //             } else if (dateObj instanceof String) {
    //                 appointmentDate = (String) dateObj;
    //             } else {
    //                 logger.warn("Unexpected date format for appointmentId: {}", doc.getString("appointmentId"));
    //                 appointmentDate = dateObj != null ? dateObj.toString() : null;
    //             }

    //             // Safely handle the timestamp
    //             String timestampStr = null;
    //             Object timestampObj = doc.get("timestamp");
    //             if (timestampObj instanceof Date) {
    //                 timestampStr = ((Date) timestampObj).toInstant().toString();
    //             } else if (timestampObj instanceof Document) {
    //                 Date embeddedDate = ((Document) timestampObj).getDate("$date");
    //                 if (embeddedDate != null) {
    //                     timestampStr = embeddedDate.toInstant().toString();
    //                 }
    //             }

    //             Appointment appointment = new Appointment(
    //                 doc.getObjectId("_id").toString(),
    //                 doc.getString("appointmentId"),
    //                 doc.getString("userId"),
    //                 doc.getString("doctorId"),
    //                 appointmentDate,
    //                 doc.getString("appointmentTime"),
    //                 doc.getString("duration"),
    //                 doc.getString("typeOfSickness"),
    //                 doc.getString("additionalNotes"),
    //                 null, // Insurance policy number
    //                 doc.getString("email"),
    //                 doc.getInteger("appointmentCost"),
    //                 doc.getString("statusPayment"),
    //                 doc.getString("statusAppointment"),
    //                 timestampStr
    //             );
    //             appointments.add(appointment);
    //         }
    
    //         logger.info("Total appointments found: {}", appointments.size());
    //     } catch (Exception e) {
    //         logger.error("Error fetching appointments: {}", e.getMessage(), e);
    //     } finally {
    //         if (mongoClient != null) {
    //             mongoClient.close();
    //         }
    //     }
    
    //     return appointments;
    // }
}