package edu.cornell.engineering.ewh.hippoandroid;

/*
* AsyncResponse interface has method processFinish that enables 
* Activities to process their AsyncCall output.
*/
public interface AsyncResponse {
    void processFinish(String output);
}