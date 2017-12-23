package com.netomation.data;

public abstract class Filter {

    public static void initFilter() {
        for(int i = 0 ; i < Globals.VALID_KEYWORDS.length ; i++)
            Globals.VALID_KEYWORDS[i] = Globals.VALID_KEYWORDS[i].toLowerCase();
        for(int i = 0 ; i < Globals.VALID_LANGUAGES.length ; i++)
            Globals.VALID_LANGUAGES[i] = Globals.VALID_LANGUAGES[i].toLowerCase();
        for(int i = 0 ; i < Globals.VALID_COUNTRIES.length ; i++)
            Globals.VALID_COUNTRIES[i] = Globals.VALID_COUNTRIES[i].toLowerCase();
    }

    public static boolean descriptionPassFilter(String description) {
        description = description.toLowerCase();
        for(String keyword : Globals.VALID_KEYWORDS) {
            if(description.contains(keyword))
                return true;
        }
        return false;
    }

    public static boolean languagePassFilter(String language) {
        language = language.toLowerCase();
        for(String lang : Globals.VALID_LANGUAGES) {
            if(language.equals(lang))
                return true;
        }
        return false;
    }

    public static boolean agePassFilter(int age) {
        return Globals.MIN_AGE_FILTER <= age && age <= Globals.MAX_AGE_FILTER;
    }

    public static boolean countryPassFilter(String country) {
        country = country.toLowerCase();
        for(String count : Globals.VALID_COUNTRIES) {
            if(country.equals(count))
                return true;
        }
        return false;
    }

}
