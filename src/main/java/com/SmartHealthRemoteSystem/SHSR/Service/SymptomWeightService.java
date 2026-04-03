package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service 
public class SymptomWeightService {

    private static final Map<String, Integer> symptomWeights = new HashMap<>();

    static {
        symptomWeights.put("itching", 1);
        symptomWeights.put("skin rash", 3);
        symptomWeights.put("nodal skin eruptions", 4);
        symptomWeights.put("continuous sneezing", 4);
        symptomWeights.put("shivering", 5);
        symptomWeights.put("chills", 3);
        symptomWeights.put("joint pain", 3);
        symptomWeights.put("stomach pain", 5);
        symptomWeights.put("acidity", 3);
        symptomWeights.put("ulcers on tongue", 4);
        symptomWeights.put("muscle wasting", 3);
        symptomWeights.put("vomiting", 5);
        symptomWeights.put("burning micturition", 6);
        symptomWeights.put("spotting urination", 6);
        symptomWeights.put("fatigue", 4);
        symptomWeights.put("weight gain", 3);
        symptomWeights.put("anxiety", 4);
        symptomWeights.put("cold hands and feets", 5);
        symptomWeights.put("mood swings", 3);
        symptomWeights.put("weight loss", 3);
        symptomWeights.put("restlessness", 5);
        symptomWeights.put("lethargy", 2);
        symptomWeights.put("patches in throat", 6);
        symptomWeights.put("irregular sugar level", 5);
        symptomWeights.put("cough", 4);
        symptomWeights.put("high fever", 7);
        symptomWeights.put("sunken eyes", 3);
        symptomWeights.put("breathlessness", 4);
        symptomWeights.put("sweating", 3);
        symptomWeights.put("dehydration", 4);
        symptomWeights.put("indigestion", 5);
        symptomWeights.put("headache", 3);
        symptomWeights.put("yellowish skin", 3);
        symptomWeights.put("dark urine", 4);
        symptomWeights.put("nausea", 5);
        symptomWeights.put("loss of appetite", 4);
        symptomWeights.put("pain behind the eyes", 4);
        symptomWeights.put("back pain", 3);
        symptomWeights.put("constipation", 4);
        symptomWeights.put("abdominal pain", 4);
        symptomWeights.put("diarrhoea", 6);
        symptomWeights.put("mild fever", 5);
        symptomWeights.put("yellow urine", 4);
        symptomWeights.put("yellowing of eyes", 4);
        symptomWeights.put("acute liver failure", 6);
        symptomWeights.put("fluid overload", 6);
        symptomWeights.put("swelling of stomach", 7);
        symptomWeights.put("swelled lymph nodes", 6);
        symptomWeights.put("malaise", 6);
        symptomWeights.put("blurred and distorted vision", 5);
        symptomWeights.put("phlegm", 5);
        symptomWeights.put("throat irritation", 4);
        symptomWeights.put("redness of eyes", 5);
        symptomWeights.put("sinus pressure", 4);
        symptomWeights.put("runny nose", 5);
        symptomWeights.put("congestion", 5);
        symptomWeights.put("chest pain", 7);
        symptomWeights.put("weakness in limbs", 7);
        symptomWeights.put("fast heart rate", 5);
        symptomWeights.put("pain during bowel movements", 5);
        symptomWeights.put("pain in anal region", 6);
        symptomWeights.put("bloody stool", 5);
        symptomWeights.put("irritation in anus", 6);
        symptomWeights.put("neck pain", 5);
        symptomWeights.put("dizziness", 4);
        symptomWeights.put("cramps", 4);
        symptomWeights.put("bruising", 4);
        symptomWeights.put("obesity", 4);
        symptomWeights.put("swollen legs", 5);
        symptomWeights.put("swollen blood vessels", 5);
        symptomWeights.put("puffy face and eyes", 5);
        symptomWeights.put("enlarged thyroid", 6);
        symptomWeights.put("brittle nails", 5);
        symptomWeights.put("swollen extremeties", 5);
        symptomWeights.put("excessive hunger", 4);
        symptomWeights.put("extra marital contacts", 5);
        symptomWeights.put("drying and tingling lips", 4);
        symptomWeights.put("slurred speech", 4);
        symptomWeights.put("knee pain", 3);
        symptomWeights.put("hip joint pain", 2);
        symptomWeights.put("muscle weakness", 2);
        symptomWeights.put("stiff neck", 4);
        symptomWeights.put("swelling joints", 5);
        symptomWeights.put("movement stiffness", 5);
        symptomWeights.put("spinning movements", 6);
        symptomWeights.put("loss of balance", 4);
        symptomWeights.put("unsteadiness", 4);
        symptomWeights.put("weakness of one body side", 4);
        symptomWeights.put("loss of smell", 3);
        symptomWeights.put("bladder discomfort", 4);
        symptomWeights.put("foul smell ofurine", 5);
        symptomWeights.put("continuous feel of urine", 6);
        symptomWeights.put("passage of gases", 5);
        symptomWeights.put("internal itching", 4);
        symptomWeights.put("toxic look (typhos)", 5);
        symptomWeights.put("depression", 3);
        symptomWeights.put("irritability", 2);
        symptomWeights.put("muscle pain", 2);
        symptomWeights.put("altered sensorium", 2);
        symptomWeights.put("red spots over body", 3);
        symptomWeights.put("belly pain", 4);
        symptomWeights.put("abnormal menstruation", 6);
        symptomWeights.put("dischromic patches", 6);
        symptomWeights.put("watering from eyes", 4);
        symptomWeights.put("increased appetite", 5);
        symptomWeights.put("polyuria", 4);
        symptomWeights.put("family history", 5);
        symptomWeights.put("mucoid sputum", 4);
        symptomWeights.put("rusty sputum", 4);
        symptomWeights.put("lack of concentration", 3);
        symptomWeights.put("visual disturbances", 3);
        symptomWeights.put("receiving blood transfusion", 5);
        symptomWeights.put("receiving unsterile injections", 2);
        symptomWeights.put("coma", 7);
        symptomWeights.put("stomach bleeding", 6);
        symptomWeights.put("distention of abdomen", 4);
        symptomWeights.put("history of alcohol consumption", 5);
        symptomWeights.put("fluid overload", 4);
        symptomWeights.put("blood in sputum", 5);
        symptomWeights.put("prominent veins on calf", 6);
        symptomWeights.put("palpitations", 4);
        symptomWeights.put("painful walking", 2);
        symptomWeights.put("pus filled pimples", 2);
        symptomWeights.put("blackheads", 2);
        symptomWeights.put("scurring", 2);
        symptomWeights.put("skin peeling", 3);
        symptomWeights.put("silver like dusting", 2);
        symptomWeights.put("small dents in nails", 2);
        symptomWeights.put("inflammatory nails", 2);
        symptomWeights.put("blister", 4);
        symptomWeights.put("red sore around nose", 2);
        symptomWeights.put("yellow crust ooze", 3);
        symptomWeights.put("prognosis", 5);
    }

    public int getSymptomWeight(String symptomName) {
        String normalized = normalizeSymptom(symptomName);
        return symptomWeights.getOrDefault(normalized, 0);
    }

    private String normalizeSymptom(String symptomName) {
        return symptomName.replace("_", " ").toLowerCase().trim();
    }
}
