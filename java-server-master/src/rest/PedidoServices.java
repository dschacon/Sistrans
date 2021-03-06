
package rest;


import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
import vos.Menu;
import vos.Pedido;
import vos.PedidoMesa;
import vos.Producto;
import vos.Video;

/**
 * Clase que expone servicios REST con ruta base: http://"ip o nombre de host":8080/RotondAndes/rest/pedidos/...
 * @author Juan Carre�o
 */
@Path("pedidos")
public class PedidoServices {

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
     * Metodo que expone servicio REST usando GET que busca el pedido con el id que entra como parametro
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/pedidos/<<id>>" para la busqueda"
     * @param name - Nombre del pedido a buscar que entra en la URL como parametro 
     * @return Json con el/los pedidos encontrados con el nombre que entra como parametro o json con 
     * el error que se produjo
     */
	@GET
	@Path( "{id: \\d+}" )
	@Produces( { MediaType.APPLICATION_JSON } )
	public Response getPedido( @PathParam( "id" ) int id )
	{
		RotondAndesTm tm = new RotondAndesTm( getPath( ) );
		try
		{
			Pedido p = tm.buscarPedidoPorId( id );
			Producto produ = tm.buscarProductoPorName(p.getNombreProducto());
			Menu menu = tm.buscarMenusPorName(p.getNombreProducto());
			if(produ!=null){
				p.setProducto(produ);
			}else{
				p.setMenu(menu);
			}
			return Response.status( 200 ).entity( p ).build( );			
		}
		catch( Exception e )
		{
			return Response.status( 500 ).entity( doErrorMessage( e ) ).build( );
		}
	}

    /**
     * Metodo que expone servicio REST usando POST que agrega el pedido que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/pedidos/pedido
     * @param pedido - pedido a agregar
     * @return Json con el pedido que agrego o Json con el error que se produjo
     */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPedido(Pedido pedido) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		Date fecha =(new Date()); 
		try {
			Producto producto = tm.buscarProductoPorName(pedido.getNombreProducto());
			Menu menu= tm.buscarMenusPorName(pedido.getNombreProducto());
			if(tm.buscarUsuarioPorId(pedido.getIdUsuario())==null){
				String error = "No existe un usuario con el id: "+pedido.getIdUsuario() ;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else if(producto==null && menu==null){
				String error = "No existe un producto o menu con el nombre de : "+pedido.getNombreProducto() ;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else{
				if(producto==null){
					pedido.setMenu(menu);
					pedido.setCostoTotal(menu.getPrecioVenta());
					try{
					tm.updateMenu(menu);
					}catch (Exception e) {
					String error = "No hay existencias de: "+pedido.getNombreProducto() ;
					return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
					}
					pedido.setRestaurante(menu.getRestaurante());
				}else{
					pedido.setProducto(producto);
					pedido.setCostoTotal(producto.getPrecioVenta());
					try{
					tm.updateProducto(producto);
					}catch (Exception e) {
					String error = "No hay existencias de: "+pedido.getNombreProducto() ;
					return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
					}
					
				}
				pedido.setRestaurante(tm.buscarRestauranteProducto(pedido.getNombreProducto()));
				pedido.setFecha(fecha);
				pedido.setId(pedido.getId());
				tm.addPedido(pedido);
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(pedido).build();
	}
	
	@POST
	@Path( "{equivalencia}" )
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPedidoEquivalencia(Pedido pedido) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		Date fecha =(new Date()); 
		ArrayList<String> equivalencias =pedido.getEquivalencias(); 
		try {
			Producto producto = tm.buscarProductoPorName(pedido.getNombreProducto());
			Menu menu= tm.buscarMenusPorName(pedido.getNombreProducto());
			if(tm.buscarUsuarioPorId(pedido.getIdUsuario())==null){
				String error = "No existe un usuario con el id: "+pedido.getIdUsuario() ;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else if(producto==null && menu==null){
				String error = "No existe un producto o menu con el nombre de : "+pedido.getNombreProducto() ;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}else{
				if(producto==null){
					pedido.setMenu(menu);
					pedido.setCostoTotal(menu.getPrecioVenta());
					try{
					tm.updateMenu(menu);
					}catch (Exception e) {
						String error = "No hay existencias de: "+pedido.getNombreProducto() ;
						return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
					}
				}else{
					pedido.setProducto(producto);
					pedido.setCostoTotal(producto.getPrecioVenta());
					try{
					tm.updateProducto(producto);
					}catch (Exception e) {
					String error = "No hay existencias de: "+pedido.getNombreProducto() ;
					return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
					}
				}
				pedido.setFecha(fecha);
				pedido.setId(1);
				pedido.setEquivalencias(equivalencias);
				tm.addPedido(pedido);
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(pedido).build();
	}

	
	
	
	@POST
	@Path( "{id: \\d+}" )
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPedidoMesa(PedidoMesa pedido) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		Date fecha =(new Date()); 
		ArrayList<String> productos =pedido.getProductos();
		ArrayList<Producto> listProductos = new ArrayList<>(); 
		ArrayList<Menu> listMenu = new ArrayList<>();
		try{
			Iterator<String> iter = productos.iterator();
			while(iter.hasNext()){
				String buscar= iter.next();
				String lresta = tm.buscarRestauranteProducto(buscar);
				Pedido pp  = new Pedido(1, 0,fecha, pedido.getIdUsuario(),buscar,lresta);
				Producto pprodu = tm.buscarProductoPorName(buscar);
				Menu lmenu=tm.buscarMenusPorName(buscar);
				if(lmenu!=null){
					listMenu.add(lmenu);
				}else{
					listProductos.add(pprodu);
				}
				this.addPedido(pp);
				pedido.setId(pp.getId());
			}
			pedido.setVoMenus(listMenu);
			pedido.setVoProductos(listProductos);
			pedido.setCostoTotal();
			pedido.setFecha(fecha);
			pedido.setNumProductos();
			tm.addPedidoMesa(pedido);
			
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(pedido).build();
	}
	
	
	
	
	
    /**
     * Metodo que expone servicio REST usando PUT que actualiza el pedido que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/pedidos
     * @param pedido - pedido a actualizar. 
     * @return Json con el pedido que actualizo o Json con el error que se produjo
     */
	@PUT
	@Path( "{id: \\d+}" )
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePedido(@PathParam( "id" ) int id ) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		Pedido pedido;
		try {
			pedido = tm.updatePedido(id);
			if(pedido==null){
				String error = "No existe un pedido con el id: "+id ;
				return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
			}
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(pedido).build();
	}
	
	
	@PUT
	@Path( "{Nombre}" )
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePedidoMesa(PedidoMesa pedido ) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
		PedidoMesa lpedido = tm.buscarPedidoMesaId(pedido.getId());
		
			for(int i=lpedido.getIdUsuario();i>0;i--){
			Pedido Cpedido = tm.updatePedido(pedido.getId()-(i-1));
				if(Cpedido==null){
					String error = "No existe un pedido con el id: "+ (pedido.getId()-(i-1));
					return Response.status(500).entity("{ \"ERROR\": \""+ error + "\"}").build();
				}
			}
		
			
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(pedido).build();
	}
	
    /**
     * Metodo que expone servicio REST usando DELETE que elimina el pedido que recibe en Json
     * <b>URL: </b> http://"ip o nombre de host":8080/RotondAndes/rest/pedidos
     * @param pedido - pedido a aliminar. 
     * @return Json con el pedido que elimino o Json con el error que se produjo
     */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePedido(Pedido pedido) {
		RotondAndesTm tm = new RotondAndesTm(getPath());
		try {
			tm.deletePedido(pedido);
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		return Response.status(200).entity(pedido).build();
	}


}
