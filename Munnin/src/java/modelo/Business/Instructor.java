/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.Business;

import java.io.File;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.http.Part;
import modelo.bean.Centro;
import modelo.bean.Producto;
import modelo.bean.TipoArchivo;
import modelo.bean.Version;
import modelo.dao.ProductoDAO;
import modelo.dao.VersionDAO;
import util.FileManager;

/**
 *
 * @author Juan David Segura
 */
public class Instructor {

    public static int uploadNewLearningObject(Part file, Producto producto, String idCentro) throws NamingException, SQLException {
        //0. error inesperado
        //1. exito
        //2. error al Insertar Objeto en la base de datos
        //3. error al Insertar Version en la base de datos
        //4. error al guardar archivo en repositorio
        //5. error al guardar ruta de archivo en base de datos
        int result = 0;

        int idProducto = 0;
        int idVersion = 0;

        //crea el registro del producto de aprendizaje en base de datos
        ProductoDAO productoDAO = new ProductoDAO();
        idProducto = productoDAO.create(producto);
        productoDAO.closeConnection();
        if (idProducto == 0) {
            return 2;
        }

        //crea el registro de la version en la base de datos
        Version version = new Version();
        version.setNumero(1);
        version.setCentro(new Centro());
        version.getCentro().setId(idCentro);
        version.setProducto(new Producto());
        version.getProducto().setId(idProducto);
        version.setTipoArchivo(new TipoArchivo());
        version.getTipoArchivo().setId(1);

        VersionDAO versionDAO = new VersionDAO();
        idVersion = versionDAO.create(version);
        versionDAO.closeConnection();
        if (idVersion == 0) {
            return 3;
        }

        //calcula la ruta en donde se guardara el archivo con el id del centro, id producto y id version
        String savePath = File.separator + "c" + idCentro + File.separator + "p" + idProducto + File.separator + "v" + idVersion;
        
        //guarda el archivo
        if (!FileManager.saveFileMunninServer(file, savePath)) {
            return 4;
        }
        //guarda la ruta en la base de datos
        version.setId(idVersion);
        version.setUrl(savePath);
        versionDAO = new VersionDAO();
        if(versionDAO.editUrl(version)){
            result = 1;
        }else{
            result = 5;
        }
        versionDAO.closeConnection();
        
        return result;
    }

}