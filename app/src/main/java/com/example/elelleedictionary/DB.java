package com.example.elelleedictionary;

public class DB {
    public static String[] getData(int id){
        if ( id == R.id.action_eng_oro ){
            return getEngOro();
        }
        else if ( id == R.id.action_oro_eng){
            return  getOroEng();
        }
        return new String[0];
    }
    public static String[] getEngOro(){
        String[] source = new String[]{
                "a",
                "aardvark",
                "aback",
                "abacus",
                "abandon",
                "abandoned",
                "abase",
                "abasement",
                "abashed",
                "abate",
                "abattoir",
                "abbess",
                "abbey",
                "abbot",
                "abbreviation",
                "abdicate",
        };
        return source;


    }
    public static String[] getOroEng(){
        String[] source = new String[]{
                "a",
                "abbaa",
                "haadha",
                "ilma",
                "mucaa",
                "qeerroo",
                "qarree",
                "jaarsa",
                "jaartii",
                "ga'eessa",
                "ga'eettii",
                "dhiira",
                "dhalaa",
                "abboo",
                "abbuyyaa",
                "abbichuu",
        };
        return source;

    }
}
