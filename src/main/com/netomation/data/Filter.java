package main.com.netomation.data;

public abstract class Filter {

    public static void initFilter() {
        for(int i = 0 ; i < Globals.FILTER_KEYWORDS.length ; i++)
            Globals.FILTER_KEYWORDS[i] = Globals.FILTER_KEYWORDS[i].toLowerCase();
        for(int i = 0 ; i < Globals.FILTER_LANGUAGES.length ; i++)
            Globals.FILTER_LANGUAGES[i] = Globals.FILTER_LANGUAGES[i].toLowerCase();
        for(int i = 0 ; i < Globals.FILTER_COUNTRIES.length ; i++)
            Globals.FILTER_COUNTRIES[i] = Globals.FILTER_COUNTRIES[i].toLowerCase();
    }

    public static boolean descriptionPassFilter(String description, boolean include) {
        if(Globals.FILTER_KEYWORDS.length == 0) {
            return true;
        }
        description = description.toLowerCase();
        for(String keyword : Globals.FILTER_KEYWORDS) {
            if(description.contains(keyword.toLowerCase()))
                return include;
        }
        return !include;
    }

    public static boolean languagePassFilter(String language, boolean include) {
        if(Globals.FILTER_LANGUAGES.length == 0) {
            return true;
        }
        language = language.toLowerCase();
        for(String lang : Globals.FILTER_LANGUAGES) {
            if(language.equals(lang))
                return include;
        }
        return !include;
    }

    public static boolean agePassFilter(int age) {
        if (Globals.MIN_AGE_FILTER < 0 && Globals.MAX_AGE_FILTER < 0) return true;
        if (Globals.MIN_AGE_FILTER > Globals.MAX_AGE_FILTER && Globals.MIN_AGE_FILTER > 0 && Globals.MAX_AGE_FILTER > 0) return false;
        if (Globals.MAX_AGE_FILTER < 0) return Globals.MIN_AGE_FILTER <= age && age >= 0;
        if (Globals.MIN_AGE_FILTER < 0) return Globals.MAX_AGE_FILTER >= age && age >= 0;
        return Globals.MIN_AGE_FILTER <= age && age <= Globals.MAX_AGE_FILTER;
    }

    public static boolean countryPassFilter(String country, boolean include) {
        if(Globals.FILTER_COUNTRIES.length == 0) {
            return true;
        }
        country = country.toLowerCase();
        for(String count : Globals.FILTER_COUNTRIES) {
            if(country.equalsIgnoreCase(count))
                return include;
        }
        return !include;
    }

}
