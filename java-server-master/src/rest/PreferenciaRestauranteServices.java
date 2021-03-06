
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
import vos.*;

/**
 * Clase que expone servicios REST con ruta base: http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes/...
 * @author Juan Carre�o
 */
@Path("PreferenciasRestaurante")
public class PreferenciaRestauranteServices {

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
     * Metodo que expone servicio REST usando GET que busca el video con el id que entra como parametro
     * <b>URL: </b> http://"ip o nombre de host":8080/VideoAndes/rest/videos/<<id>>" para la busqueda"
     * @param name - Nombre del video a buscar que entra en la URL como parametro 
     * @return Json con el/los videos encontrados con el nombre que entra como parametro o json con 
     * el error que se produjo
     */
	@GET
	@Path( "{id: \\d+}" )
	@Produces( { MediaType.APPLICATION_JSON } )
	public Response getPreferencia( @PathParam( "id" ) Integer id )
	{
		RotondAndesTm tm = new RotondAndesTm(getPath());
		List<Restaurante> preferencias = null;
		try {
			Usuario usuario = tm.buscarUsuarioPorId(id);
			if(usuario==null){
			preferencias = tm.darPreferenciasRestaurante(id);
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(preferencias).build();
	}

    /**
     * Metodo que expone servicio REST usando POST que agrega el restaurante que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes/restaurante
     * @param preferencia - restaurante a agregar
     * @return Json con el restaurante que agrego o Json con el error que se produjo
     */
	@POST
	@Path( "{id: \\d+}" )
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPreferencia(@PathParam( "id" ) Integer id ,PreferenciaRestaurante preferencia) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			Usuario usuario = tm.buscarUsuarioPorId(id);
			if(usuario==null){
				String error = "No existe un usuario con el id: "+id;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else{
			preferencia.setNumeroUsuario(id);
			tm.addPreferenciaRestaurante(preferencia);
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(preferencia).build();
	}

//    /**
//     * Metodo que expone servicio REST usando PUT que actualiza el restaurante que recibe en Json
//     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes
//     * @param preferencia - restaurante a actualizar. 
//     * @return Json con el restaurante que actualizo o Json con el error que se produjo
//     */
//	@PUT
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response updatePreferencia(PreferenciaRestaurante preferencia) {
//		RotondAndesTm tm = new RotondAndesTm(getPath());
//		try {
//			tm.updatePreferenciaRestaurante(preferencia);
//		} catch (Exception e) {
//			return Response.status(500).entity(doErrorMessage(e)).build();
//		}
//		return Response.status(200).entity(preferencia).build();
//	}
//	
//    /**
//     * Metodo que expone servicio REST usando DELETE que elimina el restaurante que recibe en Json
//     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/restaurantes
//     * @param preferencia - restaurante a aliminar. 
//     * @return Json con el restaurante que elimino o Json con el error que se produjo
//     */
//	@DELETE
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response deletePreferencia(PreferenciaRestaurante preferencia) {
//		RotondAndesTm tm = new RotondAndesTm(getPath());
//		try {
//			tm.deletePreferenciaRestaurante(preferencia);
//		} catch (Exception e) {
//			return Response.status(500).entity(doErrorMessage(e)).build();
//		}
//		return Response.status(200).entity(preferencia).build();
//	}


}
