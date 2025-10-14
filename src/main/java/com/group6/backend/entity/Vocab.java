package com.group6.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;

//This entity file will represent a table we have in our database 
// it will also communicate with Hibernate to create the schema for our database

//Word table defined in entity file
@Entity
@Table(name == "words")

//We then will define what a word will consist off and what it will include
public class Vocab {

    //The word will need an ID

    //We will need to make sure that the word is a String and not left blank
    //We also need to set the size of the String for the word

    //We also need to make sure that the definition of the word is a string and not blank
    //We also need to set the size of the String for the definition


    //Constructors
    public Word(){

    }

    public Word(String word, String definition){
        this.word = word;
        this.definition = definition;
    }

    //Getters and Setters
    public Long getID(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getWord(){
        return word;
    }

    public void setWord(String word, String definition){
        this.word = word;
    }

    public String getDefinition(){
        return definition;
    }

    public void setDefinition(){
         this.definition = definition;
    }



}



