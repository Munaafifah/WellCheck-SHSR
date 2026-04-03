package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.*;

public class DiseasePrecautionService {

    private static final Map<String, List<String>> diseasePrecautions = new HashMap<>();

    static {
        diseasePrecautions.put("Drug Reaction", Arrays.asList(
                "stop irritation",
                "consult nearest hospital",
                "stop taking drug",
                "follow up"
        ));

        diseasePrecautions.put("Malaria", Arrays.asList(
                "Consult nearest hospital",
                "avoid oily food",
                "avoid non veg food",
                "keep mosquitos out"
        ));

        diseasePrecautions.put("Allergy", Arrays.asList(
                "apply calamine",
                "cover area with bandage",
                "use ice to compress itching"
        ));

        diseasePrecautions.put("Hypothyroidism", Arrays.asList(
                "reduce stress",
                "exercise",
                "eat healthy",
                "get proper sleep"
        ));

        diseasePrecautions.put("Psoriasis", Arrays.asList(
                "wash hands with warm soapy water",
                "stop bleeding using pressure",
                "consult doctor",
                "salt baths"
        ));

        diseasePrecautions.put("GERD", Arrays.asList(
                "avoid fatty spicy food",
                "avoid lying down after eating",
                "maintain healthy weight",
                "exercise"
        ));

        diseasePrecautions.put("Chronic cholestasis", Arrays.asList(
                "cold baths",
                "anti itch medicine",
                "consult doctor",
                "eat healthy"
        ));

        diseasePrecautions.put("hepatitis A", Arrays.asList(
                "Consult nearest hospital",
                "wash hands through",
                "avoid fatty spicy food",
                "medication"
        ));

        diseasePrecautions.put("Osteoarthristis", Arrays.asList(
                "acetaminophen",
                "consult nearest hospital",
                "follow up",
                "salt baths"
        ));

        diseasePrecautions.put("(vertigo) Paroymsal  Positional Vertigo", Arrays.asList(
                "lie down",
                "avoid sudden change in body",
                "avoid abrupt head movment",
                "relax"
        ));

        diseasePrecautions.put("Hypoglycemia", Arrays.asList(
                "lie down on side",
                "check in pulse",
                "drink sugary drinks",
                "consult doctor"
        ));

        diseasePrecautions.put("Acne", Arrays.asList(
                "bath twice",
                "avoid fatty spicy food",
                "drink plenty of water",
                "avoid too many products"
        ));

        diseasePrecautions.put("Diabetes ", Arrays.asList(
                "have balanced diet",
                "exercise",
                "consult doctor",
                "follow up"
        ));

        diseasePrecautions.put("Impetigo", Arrays.asList(
                "soak affected area in warm water",
                "use antibiotics",
                "remove scabs with wet compressed cloth",
                "consult doctor"
        ));

        diseasePrecautions.put("Hypertension ", Arrays.asList(
                "meditation",
                "salt baths",
                "reduce stress",
                "get proper sleep"
        ));

        diseasePrecautions.put("Peptic ulcer diseae", Arrays.asList(
                "avoid fatty spicy food",
                "consume probiotic food",
                "eliminate milk",
                "limit alcohol"
        ));

        diseasePrecautions.put("Dimorphic hemmorhoids(piles)", Arrays.asList(
                "avoid fatty spicy food",
                "consume witch hazel",
                "warm bath with epsom salt",
                "consume alovera juice"
        ));

        diseasePrecautions.put("Common Cold", Arrays.asList(
                "drink vitamin c rich drinks",
                "take vapour",
                "avoid cold food",
                "keep fever in check"
        ));

        diseasePrecautions.put("Chicken pox", Arrays.asList(
                "use neem in bathing",
                "consume neem leaves",
                "take vaccine",
                "avoid public places"
        ));

        diseasePrecautions.put("Cervical spondylosis", Arrays.asList(
                "use heating pad or cold pack",
                "exercise",
                "take otc pain reliver",
                "consult doctor"
        ));

        diseasePrecautions.put("Hyperthyroidism", Arrays.asList(
                "eat healthy",
                "massage",
                "use lemon balm",
                "take radioactive iodine treatment"
        ));

        diseasePrecautions.put("Urinary tract infection", Arrays.asList(
                "drink plenty of water",
                "increase vitamin c intake",
                "drink cranberry juice",
                "take probiotics"
        ));

        diseasePrecautions.put("Varicose veins", Arrays.asList(
                "lie down flat and raise the leg high",
                "use oinments",
                "use vein compression",
                "dont stand still for long"
        ));

        diseasePrecautions.put("AIDS", Arrays.asList(
                "avoid open cuts",
                "wear ppe if possible",
                "consult doctor",
                "follow up"
        ));

        diseasePrecautions.put("Paralysis (brain hemorrhage)", Arrays.asList(
                "massage",
                "eat healthy",
                "exercise",
                "consult doctor"
        ));

        diseasePrecautions.put("Typhoid", Arrays.asList(
                "eat high calorie vegitables",
                "antiboitic therapy",
                "consult doctor",
                "medication"
        ));

        diseasePrecautions.put("Hepatitis B", Arrays.asList(
                "consult nearest hospital",
                "vaccination",
                "eat healthy",
                "medication"
        ));

        diseasePrecautions.put("Fungal infection", Arrays.asList(
                "bath twice",
                "use detol or neem in bathing water",
                "keep infected area dry",
                "use clean cloths"
        ));

        diseasePrecautions.put("Hepatitis C", Arrays.asList(
                "Consult nearest hospital",
                "vaccination",
                "eat healthy",
                "medication"
        ));

        diseasePrecautions.put("Migraine", Arrays.asList(
                "meditation",
                "reduce stress",
                "use poloroid glasses in sun",
                "consult doctor"
        ));

        diseasePrecautions.put("Bronchial Asthma", Arrays.asList(
                "switch to loose cloothing",
                "take deep breaths",
                "get away from trigger",
                "seek help"
        ));

        diseasePrecautions.put("Alcoholic hepatitis", Arrays.asList(
                "stop alcohol consumption",
                "consult doctor",
                "medication",
                "follow up"
        ));

        diseasePrecautions.put("Jaundice", Arrays.asList(
                "drink plenty of water",
                "consume milk thistle",
                "eat fruits and high fiberous food",
                "medication"
        ));

        diseasePrecautions.put("Hepatitis E", Arrays.asList(
                "stop alcohol consumption",
                "rest",
                "consult doctor",
                "medication"
        ));

        diseasePrecautions.put("Dengue", Arrays.asList(
                "drink papaya leaf juice",
                "avoid fatty spicy food",
                "keep mosquitos away",
                "keep hydrated"
        ));

        diseasePrecautions.put("Hepatitis D", Arrays.asList(
                "consult doctor",
                "medication",
                "eat healthy",
                "follow up"
        ));

        diseasePrecautions.put("Heart attack", Arrays.asList(
                "call ambulance",
                "chew or swallow asprin",
                "keep calm"
        ));

        diseasePrecautions.put("Pneumonia", Arrays.asList(
                "consult doctor",
                "medication",
                "rest",
                "follow up"
        ));

        diseasePrecautions.put("Arthritis", Arrays.asList(
                "exercise",
                "use hot and cold therapy",
                "try acupuncture",
                "massage"
        ));

        diseasePrecautions.put("Gastroenteritis", Arrays.asList(
                "stop eating solid food for while",
                "try taking small sips of water",
                "rest",
                "ease back into eating"
        ));

        diseasePrecautions.put("Tuberculosis", Arrays.asList(
                "cover mouth",
                "consult doctor",
                "medication",
                "rest"
        ));
    }

    public List<String> getPrecautionsForDisease(String diseaseName) {
        return diseasePrecautions.getOrDefault(diseaseName, Collections.singletonList("No specific precautions available"));
    }
}
