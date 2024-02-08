package get.wordy.model;

public record WordRequest(String value,
                          String partOfSpeech,
                          String transcription,
                          String meaning
) {

}