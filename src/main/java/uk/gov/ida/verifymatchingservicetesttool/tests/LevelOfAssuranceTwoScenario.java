package uk.gov.ida.verifymatchingservicetesttool.tests;

import org.junit.Test;
import uk.gov.ida.verifymatchingservicetesttool.utils.FileUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;

public class LevelOfAssuranceTwoScenario {

    private FileUtils fileUtils = new FileUtils();
    private Client client = ClientBuilder.newClient();

    @Test
    public void runForWhenAllElementsAreVerifiedAndNoMultipleValues() throws IOException {
        Response response = client.target("http://localhost:50130/local-matching/match")
            .request("application/json")
            .post(Entity.json(fileUtils.readFromResources("LoA2-simple-case.json")));

        Map<String, String> result = response.readEntity(new GenericType<Map<String, String>>() {{ }});

        assertThat(result.keySet(), is(new HashSet<String>() {{ add("result"); }}));
        assertThat(result.get("result"), anyOf(is("match"), is("no-match")));
    }

    @Test
    public void runForExtensiveCase() throws IOException {
        String jsonString = fileUtils.readFromResources("LoA2-extensive-case.json")
            .replace("%yesterdayDate%", Instant.now().minus(1, DAYS).toString())
            .replace("%within405days-100days%", Instant.now().minus(405-100, DAYS).toString())
            .replace("%within405days-101days%", Instant.now().minus(405-101, DAYS).toString())
            .replace("%within405days-200days%", Instant.now().minus(405-200, DAYS).toString())
            .replace("%within180days-100days%", Instant.now().minus(105-100, DAYS).toString())
            .replace("%within180days-101days%", Instant.now().minus(105-101, DAYS).toString())
            .replace("%within180days-150days%", Instant.now().minus(105-150, DAYS).toString());

        Response response = client.target("http://localhost:50130/local-matching/match")
            .request("application/json")
            .post(Entity.json(jsonString));

        Map<String, String> result = response.readEntity(new GenericType<Map<String, String>>() {{ }});

        assertThat(result.keySet(), is(new HashSet<String>() {{ add("result"); }}));
        assertThat(result.get("result"), anyOf(is("match"), is("no-match")));
    }
}
