package get.wordy.rest;

import get.wordy.ai.AiService;
import get.wordy.ai.model.GetExplanationResult;
import get.wordy.model.WordSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/words")
public class WordsController {
    private static final Logger LOG = LoggerFactory.getLogger(WordsController.class);

    private final AiService aiService;

    public WordsController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping
    public ResponseEntity<WordSearchResponse> getWordSheet(Principal user,
                                                           @RequestParam(value = "value") String searchRequest) {
        LOG.info("Starting search initiation for the word/phrase = {}, by user = {}", searchRequest, user.getName());
        GetExplanationResult searchResult = aiService.requestWordSearch(searchRequest);
        return new ResponseEntity<>(toResponse(searchResult), HttpStatus.OK);
    }

    private WordSearchResponse toResponse(GetExplanationResult searchResult) {
        return WordSearchResponse.builder()
                .value(searchResult.getValue())
                .transcription(searchResult.getTranscription())
                .explanations(searchResult
                        .getExplanations()
                        .stream()
                        .map(res -> WordSearchResponse.ExplanationResponse.builder()
                                .partOfSpeech(res.getPartOfSpeech())
                                .meaning(res.getMeaning())
                                .inContext(res.getSentences())
                                .collocations(res.getCollocations())
                                .build()
                        )
                        .toList()
                )
                .build();
    }

}
