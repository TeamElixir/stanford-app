package org.elixir.controllers;

public class SimpleRelationsController {
    private static final String NO_RELATION = "No Relation";
    private static final String ELABORATION = "Elaboration";
    private static final String REDUNDANCY = "Redundancy";
    private static final String CITATION = "Citation";
    private static final String SHIFT_IN_VIEW = "Shift-in-View";

    public static String getSimpleRelation(int id) {
        switch (id) {
            case 1:
                return NO_RELATION;
            case 2:
                return ELABORATION;
            case 3:
                return REDUNDANCY;
            case 4:
                return CITATION;
            case 5:
                return SHIFT_IN_VIEW;
            default:
                return null;
        }
    }
}
