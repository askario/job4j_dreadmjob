package ru.job4j.dream.servlet;

import lombok.SneakyThrows;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;
import ru.job4j.dream.store.StoreStub;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlStore.class)
public class PostServletTest {
    @Test
    @SneakyThrows
    public void whenAddPostThenStoreIt() {
        Store store = new StoreStub();
        PowerMockito.mockStatic(PsqlStore.class);
        when(PsqlStore.instOf()).thenReturn(store);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getParameter("id")).thenReturn("1");
        when(req.getParameter("name")).thenReturn("Java Developer");
        when(req.getParameter("description")).thenReturn("Java,SQL,JS");

        new PostServlet().doPost(req,resp);

        Assert.assertThat(store.findAllPosts().iterator().next().getName(), Is.is("Java Developer"));
    }
}
