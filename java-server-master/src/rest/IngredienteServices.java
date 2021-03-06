
package rest;


import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

import tm.RotondAndesTm;
import vos.Ingrediente;
import vos.Restaurante;

/**
 * Clase que expone servicios REST con ruta base: http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes/...
 * @author Juan Carre�o
 */
@Path("ingredientes")
public class IngredienteServices {

	/**
	 * Atributo que usa la anotacion @Context para tener el ServletContext de la conexion actual.
	 */
	@Context
	private ServletContext context;

	/**
	 * Metodo que retorna el path de la carpeta WEB-INF/ConnectionData en el deploy actual dentro del servidor.
	 * @return path de la carpeta WEB-INF/ConnectionData en el deploy actual.
	 */
	private String getPath() {
		return context.getRealPath("WEB-INF/ConnectionData");
	}
	
	
	private String doErrorMessage(Exception e){
		return "{ \"ERROR\": \""+ e.getMessage() + "\"}" ;
	}
	

	/**
	 * Metodo que expone servicio REST usando GET que da todos los ingredientes de la base de datos.
	 * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/ingredientes
	 * @return Json con todos los ingredientes de la base de datos o json con 
     * el error que se produjo
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getIngredientes() {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		List<Ingrediente> ingredientes;
		try {
			ingredientes = tm.darIngredientes();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(ingredientes).build();
	}

    /**
     * Metodo que expone servicio REST usando GET que busca el ingrediente con el nombre que entra como parametro
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/ingredientes/nombre/nombre?nombre=<<nombre>>" para la busqueda"
     * @param name - Nombre del ingrediente a buscar que entra en la URL como parametro 
     * @return Json con el/los ingredientes encontrados con el nombre que entra como parametro o json con 
     * el error que se produjo
     */
	@GET
	@Path( "{nombre}" )
	@Produces( { MediaType.APPLICATION_JSON } )
	public Response getIngredienteName( @PathParam("nombre") String name) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		List<Ingrediente> ingredientes;
		try {
			if (name == null || name.length() == 0)
				throw new Exception("Nombre del restaurante no valido");
			ingredientes = tm.buscarIngredientesPorName(name);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(ingredientes).build();
	}


    /**
     * Metodo que expone servicio REST usando POST que agrega el ingrediente que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/ingredientes/ingrediente
     * @param ingrediente - ingrediente a agregar
     * @return Json con el ingrediente que agrego o Json con el error que se produjo
     */
	@POST
	@Path( "{nombre}" )
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addIngrediente(@PathParam( "nombre" ) String nombreRestaurante , Ingrediente ingrediente) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			Restaurante restaurante = tm.buscarRestaurantesPorName(nombreRestaurante);
			if(restaurante==null){
				String error = "No existe un restaurante con el nombre: "+nombreRestaurante;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else{
			tm.addIngrediente(ingrediente);
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(ingrediente).build();
	}

    /**
     * Metodo que expone servicio REST usando PUT que actualiza el ingrediente que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/ingredientes
     * @param ingrediente - ingrediente a actualizar. 
     * @return Json con el ingrediente que actualizo o Json con el error que se produjo
     */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateIngrediente(Ingrediente ingrediente) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			tm.updateIngrediente(ingrediente);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(ingrediente).build();
	}
	
    /**
     * Metodo que expone servicio REST usando DELETE que elimina el ingrediente que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/ingredientes
     * @param ingrediente - ingrediente a aliminar. 
     * @return Json con el ingrediente que elimino o Json con el error que se produjo
     */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteIngrediente(Ingrediente ingrediente) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			tm.deleteIngrediente(ingrediente);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(ingrediente).build();
	}


}
