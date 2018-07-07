package org.elixir.api;

import org.elixir.ArgumentTreeGenerator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/arguments")
public class DataService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ArrayList<ArrayList<String>>> getArguments() {
        return ArgumentTreeGenerator.getExtractedArguments();
    }
}
