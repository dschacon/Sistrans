
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
import vos.Producto;
import vos.Restaurante;
import vos.Usuario;

/**
 * Clase que expone servicios REST con ruta base: http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes/...
 * @author Juan Carre�o
 */
@Path("restaurantes")
public class RestauranteServices {

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
	 * Metodo que expone servicio REST usando GET que da todos los restaurantes de la base de datos.
	 * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes
	 * @return Json con todos los restaurantes de la base de datos o json con 
     * el error que se produjo
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRestaurantes() {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		List<Restaurante> restaurantes;
		try {
			restaurantes = tm.darRestaurantes();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(restaurantes).build();
	}

    /**
     * Metodo que expone servicio REST usando GET que busca el restaurante con el nombre que entra como parametro
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes/nombre/nombre?nombre=<<nombre>>" para la busqueda"
     * @param name - Nombre del restaurante a buscar que entra en la URL como parametro 
     * @return Json con el/los restaurantes encontrados con el nombre que entra como parametro o json con 
     * el error que se produjo
     */
	@GET
	@Path( "{nombre}" )
	@Produces( { MediaType.APPLICATION_JSON } )
	public Response getRestauranteName( @PathParam("nombre") String name) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		Restaurante restaurantes;
		try {
			if (name == null || name.length() == 0)
				throw new Exception("Nombre del restaurante no valido");
			restaurantes = tm.buscarRestaurantesPorName(name);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(restaurantes).build();
	}


    /**
     * Metodo que expone servicio REST usando POST que agrega el restaurante que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes/restaurante
     * @param restaurante - restaurante a agregar
     * @return Json con el restaurante que agrego o Json con el error que se produjo
     */
	@POST
	@Path( "{id: \\d+}" )
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addRestaurante(@PathParam( "id" ) Integer id ,Restaurante restaurante) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			Usuario admin = tm.buscarUsuarioPorId(id);
			if(admin==null){
				String error = "No existe un administrador con el id: "+id;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else if(!(admin.getRol().toUpperCase().trim()).equals("ADMINISTRADOR")){
				String error = "Un restaurante solo puede ser a�adido por un administrador";
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else{
			tm.addRestaurante(restaurante);
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(restaurante).build();
	}

    /**
     * Metodo que expone servicio REST usando PUT que actualiza el restaurante que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes
     * @param restaurante - restaurante a actualizar. 
     * @return Json con el restaurante que actualizo o Json con el error que se produjo
     * @throws Exception 
     */
	@PUT
	@Path( "{nombre}" )
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateRestaurante( @PathParam( "nombre" ) String nombre) throws Exception {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		Producto producto = tm.buscarProductoPorName(nombre);
		try {
			if(producto==null){
				String error = "No existe un producto con ese nombre";
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}
			tm.updateRestaurante(nombre);
			producto = tm.buscarProductoPorName(nombre);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(producto).build();
	}
	
    /**
     * Metodo que expone servicio REST usando DELETE que elimina el restaurante que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes
     * @param restaurante - restaurante a aliminar. 
     * @return Json con el restaurante que elimino o Json con el error que se produjo
     */
	@DELETE
	@Path("{nombre}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRestaurante(@PathParam( "nombre" ) String nombre) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			tm.deleteRestauranteRemote(nombre);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(nombre).build();
	}


}
