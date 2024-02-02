package get.wordy.webapp;

import com.fasterxml.jackson.databind.json.JsonMapper;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.bean.Dictionary;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/api/v1/dictionaries", loadOnStartup = 1)
public class DictionariesServlet extends HttpServlet {

    private IDictionaryService dictionaryService;
    private JsonMapper jsonMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("DictionariesServlet \"Init\" method called");

        dictionaryService = ServiceHolder.getDictionaryService();
        jsonMapper = new JsonMapper();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {

        List<Dictionary> dictionaries = dictionaryService.getDictionaries();
        String listAsString = jsonMapper.writeValueAsString(dictionaries);

        // write dictionaries to response
        resp.setContentType("application/json");
        resp.getWriter().write(listAsString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

    }

}