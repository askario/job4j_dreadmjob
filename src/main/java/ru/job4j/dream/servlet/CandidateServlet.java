package ru.job4j.dream.servlet;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Photo;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class CandidateServlet extends HttpServlet {
    private final Store psqlStore = PsqlStore.instOf();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", new ArrayList<>(PsqlStore.instOf().findAllCandidates()));
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        Candidate candidate = new Candidate();

        try {
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("images");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    if (!item.getName().isEmpty()) {
                        String unique = UUID.randomUUID() + item.getName();
                        File file = new File(folder + File.separator + unique);

                        try (FileOutputStream out = new FileOutputStream(file)) {
                            out.write(item.getInputStream().readAllBytes());
                        }

                        Photo photo = new Photo(unique);
                        candidate.setPhoto(photo);
                    }
                } else {
                    if (item.getFieldName().equals("name")) {
                        candidate.setName(item.getString());
                    }
                    if (item.getFieldName().equals("city")) {
                        candidate.setCity(new City(Integer.valueOf(item.getString()), null));
                    }
                }
            }
        } catch (FileUploadException e) {
            log.error("Error while uploading image", e);
        }

        Optional<Integer> candidateId = Optional.ofNullable(Integer.valueOf(req.getParameter("id")));
        candidateId.ifPresent(id -> candidate.setId(id));

        psqlStore.save(candidate);
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
