/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Business;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.mail.MessagingException;
import javax.naming.NamingException;
import model.bean.Area;
import model.bean.Centro;
import model.bean.Funcionario;
import model.bean.Notificacion;
import model.bean.Programa;
import model.bean.Rol;
import model.bean.TipoDocumento;
import model.dao.AreaDAO;
import model.dao.TipoDocumentoDAO;
import util.Encriptado;
import util.PassGenerator;
import util.Mail;
import model.dao.FuncionarioDAO;
import model.dao.NotificacionDAO;
import model.dao.ProgramaDAO;
import model.dao.RolDAO;

/**
 * clase dedicada a la logica de negocio usada por el rol de coordinador
 *
 * @author Juan David Segura Castro
 *
 */
public class Coordinator {

    /**
     * registro de funcionarios con la logica de un admin
     *
     * @param funcionario Datos del funcionario a registerFunctionary
     * @param idCenter Id del centro en que va a ser registrado
     * @return un entero entre 0 y 5. 0 fallo. 1 completado. 2 existe un usuario
     * activo con los datos ingresados. 3 existe un usuario no-activo con el
     * mismo correo. 4 existe un usuario no-activo con el mismo documento. 5 el
     * correo no pudo ser enviado.
     * @throws util.Encriptado.CannotPerformOperationException Error al realizar
     * la encriptacion de la contraseña, verificar la version de java
     * @throws NamingException Error en el constructor ConexionBD
     * @throws SQLException Error en el constructor ConexionBD
     * @throws java.io.UnsupportedEncodingException Problemas con los Correos de
     * origen
     * @throws javax.mail.MessagingException Problemas con el Correo de destino
     */
    public static int registerFunctionary(Funcionario funcionario, String idCenter) throws Encriptado.CannotPerformOperationException, NamingException, SQLException, UnsupportedEncodingException, MessagingException {
        /*
        0. fallo
        1. completado
        2. existe un usuario activo con los datos ingresados
        3. existe un usuario no-activo con el mismo correo
        4. existe un usuario no-activo con el mismo documento
        5. el correo no pudo ser enviado
         */

        int resultado = 0;
        Centro centro = new Centro();
        centro.setId(idCenter);
        funcionario.setCentro(centro);
        String contrasena = PassGenerator.getSecurePassword();
        funcionario.setContrasena(Encriptado.createHash(contrasena));
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

        if (funcionarioDAO.isActiveFunctionary(funcionario.getCorreo(), funcionario.getTipoDocumento().getId(), funcionario.getDocumento())) {
            resultado = 2;
        } else {
            if (funcionarioDAO.existFunctionaryByMail(funcionario.getCorreo())) {
                resultado = 3;
            } else if (funcionarioDAO.existFunctionaryByDocument(funcionario.getDocumento(), funcionario.getTipoDocumento().getId())) {
                resultado = 4;
            } else {
                if (funcionarioDAO.registerFunctionary(funcionario)) {
                    funcionario.setContrasena(null);
                    if (Mail.enviarPrimeraContrasena(funcionario, contrasena)) {
                        resultado = 1;
                    } else {
                        resultado = 5;
                    }
                }
            }
        }

        funcionarioDAO.closeConnection();

        return resultado;
    }

    /**
     * consulta de los tipos de documento
     *
     * @return ArrayList de tipos de documento
     * @throws NamingException Error en el constructor ConexionBD
     * @throws SQLException Error en el constructor ConexionBD o en el query de
     * la consulta
     */
    public static ArrayList<TipoDocumento> viewDocumentType() throws NamingException, SQLException {
        ArrayList<TipoDocumento> tiposDoc;
        TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
        tiposDoc = tipoDocumentoDAO.selectAll();

        tipoDocumentoDAO.closeConnection();

        return tiposDoc;
    }

    /**
     * Consulta los roles que son posibles asignar desde el rol de coordinador
     *
     * @return Un arreglo de Roles con su respectivo id y nombre
     * @throws NamingException
     * @throws SQLException
     */
    public static ArrayList<Rol> viewRoles() throws NamingException, SQLException {
        ArrayList<Rol> roles;

        RolDAO rolDAO = new RolDAO();
        roles = rolDAO.selectAll();
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getId() == RolDAO.ID_ADMINISTRADOR) {
                roles.remove(i);
            }
        }

        rolDAO.closeConnection();

        return roles;
    }
    
    public static int countPagesAreasCenter(String idCenter, int resultsInPage, String search) throws NamingException, SQLException {
        int paginas;
        int countAreas;
        AreaDAO areaDAO = new AreaDAO();
        countAreas = areaDAO.countAreasCenter(idCenter, search,true);
        areaDAO.closeConnection();
        paginas = countAreas / resultsInPage;
        if (countAreas % resultsInPage != 0) {
            paginas++;
        }

        return paginas;
    }
    
    public static int registerArea(Area area, String idCenter) throws Encriptado.CannotPerformOperationException, NamingException, SQLException, UnsupportedEncodingException, MessagingException {
        /*
        0. fallo
        1. completado
        2. existe un usuario activo con los datos ingresados
        3. existe un usuario no-activo con el mismo correo
        4. existe un usuario no-activo con el mismo documento
        5. el correo no pudo ser enviado
         */

        int resultado = 0;
        Centro centro = new Centro();
        centro.setId(idCenter);
        area.setCentro(centro);
        AreaDAO areaDAO = new AreaDAO();
        if (areaDAO.isNameAreaOcuped(area.getNombre(), idCenter)) {
            resultado = 2;
        } else if (areaDAO.registerArea(area)) {
            resultado = 1;
        }

        areaDAO.closeConnection();

        return resultado;
    }
    
    public static ArrayList<Area> viewAreasCenter(String idCenter, int pagina, int cantXpag, String search) throws NamingException, SQLException {
        ArrayList<Area> area;
        AreaDAO areaDAO = new AreaDAO();
        area = areaDAO.selectSomeAreasCenter(idCenter, pagina, cantXpag, search, true);

        areaDAO.closeConnection();

        return area;
    }

    /**
     * Consulta la cantidad de paginas necesarias para mostrar todos los
     * funcionarios
     *
     * @param idCenter Id del centro al cual se va a realizar la consulta
     * @param resultsInPage Cantidad de funcionarios por pagina
     * @param search filtro de busqueda
     * @return la cantidad de paginas
     * @throws NamingException Error en el constructor ConexionBD
     * @throws SQLException Error en el constructor ConexionBD o en el query de
     * la consulta
     */
    public static int countPagesFunctionariesCenter(String idCenter, int resultsInPage, String search) throws NamingException, SQLException {
        int paginas;
        int cantFuncionarios;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        cantFuncionarios = funcionarioDAO.countFunctionariesCenter(idCenter, search, true);
        funcionarioDAO.closeConnection();

        paginas = cantFuncionarios / resultsInPage;
        if (cantFuncionarios % resultsInPage != 0) {
            paginas++;
        }

        return paginas;
    }
    
    public static int countPagesFunctionariesDisabledCenter(String idCenter, int resultsInPage, String search) throws NamingException, SQLException {
        int paginas;
        int cantFuncionarios;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        cantFuncionarios = funcionarioDAO.countFunctionariesCenter(idCenter, search, false);
        funcionarioDAO.closeConnection();

        paginas = cantFuncionarios / resultsInPage;
        if (cantFuncionarios % resultsInPage != 0) {
            paginas++;
        }

        return paginas;
    }

    /**
     * Consulta los funcionarios de un centro en especifico, en una pagina
     * especifica
     *
     * @param idCenter Id del centro a consultar
     * @param pagina El numero de la pagina a consultar
     * @param cantXpag La cantidad de funcionarios por pagina
     * @param search filtro de busqueda de funcionario, por nombre, apellido,
     * documento
     * @return
     * @throws NamingException Error en el constructor ConexionBD
     * @throws SQLException Error en el constructor ConexionBD o en el query de
     * la consulta
     */
    public static ArrayList<Funcionario> viewFunctionariesCenter(String idCenter, int pagina, int cantXpag, String search) throws NamingException, SQLException {
        ArrayList<Funcionario> funcionarios;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        funcionarios = funcionarioDAO.selectSomeFunctionariesCenter(idCenter, pagina, cantXpag, search, true);

        funcionarioDAO.closeConnection();

        return funcionarios;
    }

    /**
     * Inhabilita la cuenta de un funcionario
     *
     * @param idFuncionario Id del funcionario a inhabilitar
     * @param idCenter
     * @return True, si el funcionario fue inhabilitado correctamente
     * @throws NamingException Error en el constructor ConexionBD
     * @throws SQLException Error en el constructor ConexionBD o en el query de
     * la consulta
     */
    public static int disableFunctionary(int idFuncionario, String idCenter) throws NamingException, SQLException {
        int resultado = 0;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        if (funcionarioDAO.isLastCoordinatorEnableCenter(idCenter,idFuncionario)) {
            resultado = 2;
        } else {
            if (funcionarioDAO.disableFunctionary(idFuncionario)) {
                resultado = 1;
            }
        }

        funcionarioDAO.closeConnection();

        return resultado;
    }

    public static boolean enableFunctionary(int idFuncionario) throws NamingException, SQLException {
        boolean resultado;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        resultado = funcionarioDAO.enableFunctionary(idFuncionario);

        funcionarioDAO.closeConnection();

        return resultado;
    }

    public static Funcionario viewAllInfoFunctionary(int idFuncionario) throws NamingException, SQLException {
        Funcionario resultado = new Funcionario();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        resultado.setId(idFuncionario);
        resultado = funcionarioDAO.select(resultado);
        resultado.setRoles(funcionarioDAO.selectRolesFunctionary(resultado.getId()));
        resultado.setContrasena(null);

        funcionarioDAO.closeConnection();

        return resultado;
    }

    /**
     * Metodo para asignar roles a los funcionarios
     *
     * @param idFuncionario id del funcionario al cual se le cambiara el rol
     * @param strIdNewRoles ArrayList de Strings con los id de los roles
     * @return un valor entero que cambia dependiendo de la operacion: 0 ha
     * ocurrido un error, 1 operacion exitosa, 2 error en el ingreso de datos, 3
     * no se ha podido agregar algun rol al funcionario, 4 no se ha podido
     * eliminar algun rol del funcionario
     * @throws NamingException
     * @throws SQLException
     */
    public static int AssignRoles(int idFuncionario, ArrayList<String> strIdNewRoles) throws NamingException, SQLException {
        int resultado = 0;
        //transforma las id de los roles de string a int
        ArrayList<Integer> intIdNewRoles = new ArrayList<>();
        for (String idRole : strIdNewRoles) {
            try {
                intIdNewRoles.add(Integer.parseInt(idRole));
            } catch (Exception e) {
                return 2;//finaliza con error, inconsistencia en los datos ingresados
            }
        }
        //consulta los roles disponibles
        ArrayList<Rol> newRoles = verifyRol(intIdNewRoles);//valida los nuevos roles(sean roles validos para asignar)
        ArrayList<Rol> availableRoles = viewRoles();//roles disponibles
        ArrayList<Rol> currentRoles = viewAllInfoFunctionary(idFuncionario).getRoles();//roles del funcionario actualmente
        ArrayList<Rol> deleteRoles = new ArrayList<>();//roles eliminados del funcionario 
        ArrayList<Rol> addRoles = new ArrayList<>();//nuevos roles del funcionario

        for (Rol availableRole : availableRoles) {//compara los roles que ya posee el funcionario y los nuevos roles con los roles disponibles para cambiar
            boolean isCurrentRole = false;
            boolean isNewRole = false;

            for (Rol currentRole : currentRoles) {
                if (currentRole.getId() == availableRole.getId()) {
                    isCurrentRole = true;
                    break;
                }
            }
            for (Rol newRole : newRoles) {
                if (availableRole.getId() == newRole.getId()) {
                    isNewRole = true;
                    break;
                }
            }

            if (isCurrentRole != isNewRole) {//si son iguales significa que no hay ningun cambio
                if (isNewRole) {//se agrega el rol al funcionario
                    addRoles.add(availableRole);
                } else {//se quita el rol al funcionario
                    deleteRoles.add(availableRole);
                }
            }
        }
        //se realizan la operaciones en la base de datos
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        for (Rol addRole : addRoles) {
            if (!funcionarioDAO.addFunctionaryRole(idFuncionario, addRole.getId())) {
                resultado = 3;
            }
        }
        for (Rol deleteRole : deleteRoles) {
            if (!funcionarioDAO.deleteFunctionaryRole(idFuncionario, deleteRole.getId())) {
                resultado = 4;
            }
        }
        if (resultado == 0) {//si resultado no cambia en ningun momento la operacion fue exitosa
            resultado = 1;//operacion exitosa
        }
        funcionarioDAO.closeConnection();

        return resultado;
    }

    /**
     * Verificacion para realizar un cambio de roles, solo por seguridad se
     * revisan nuevamente los roles disponibles y se comparan con los roles que
     * vana ser cambiados
     *
     * @param intIdRoles Arreglo de enteros con el id de los roles con los que
     * el funcionario va a quedar
     * @return Arreglo de roles
     * @throws NamingException
     * @throws SQLException
     */
    private static ArrayList<Rol> verifyRol(ArrayList<Integer> intIdRoles) throws NamingException, SQLException {
        ArrayList<Rol> answer = new ArrayList<>();

        ArrayList<Rol> availableRoles = viewRoles();

        classify:
        for (Rol availableRole : availableRoles) {
            for (Integer intIdRole : intIdRoles) {
                if (availableRole.getId() == intIdRole) {
                    answer.add(availableRole);
                    continue classify;
                }
            }
        }
        return answer;
    }

    public static ArrayList<Funcionario> viewFunctionariesDisabledCenter(String idCenter, int pagina, int cantXpag, String search) throws NamingException, SQLException {
        ArrayList<Funcionario> funcionarios;
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        funcionarios = funcionarioDAO.selectSomeFunctionariesCenter(idCenter, pagina, cantXpag, search, false);

        funcionarioDAO.closeConnection();

        return funcionarios;
    }
    
    
    public static int updateArea(Area area, String idCenter) throws Encriptado.CannotPerformOperationException, NamingException, SQLException, UnsupportedEncodingException, MessagingException {
        /*
        0. fallo
        1. completado
        2. existe un usuario activo con los datos ingresados
        3. existe un usuario no-activo con el mismo correo
        4. existe un usuario no-activo con el mismo documento
        5. el correo no pudo ser enviado
         */

        int resultado = 0;
        Centro centro = new Centro();
        centro.setId(idCenter);
        area.setCentro(centro);
        AreaDAO areaDAO = new AreaDAO();
        if (areaDAO.isNameAreaOcuped(area.getNombre(),idCenter)) {
            resultado = 2;
        } else if (areaDAO.updateArea(area)) {
            resultado = 1;
        }

        areaDAO.closeConnection();

        return resultado;
    }
    
    public static boolean enableArea(int idFuncionario) throws NamingException, SQLException {
        boolean resultado;
        AreaDAO areaDAO = new AreaDAO();
        resultado = areaDAO.enableArea(idFuncionario);

        areaDAO.closeConnection();

        return resultado;
    }
    
    public static int disableArea(int idArea, String idCenter) throws NamingException, SQLException {
        int resultado = 0;
        AreaDAO areaDAO = new AreaDAO();
        if (areaDAO.isLastAreaEnableArea(idCenter, idArea)) {
            resultado = 2;
        } else if (areaDAO.disableArea(idArea)) {
            resultado = 1;
        }

        areaDAO.closeConnection();

        return resultado;
    }
    
    public static Area viewAllInfoArea(int idArea) throws NamingException, SQLException {
        Area resultado = new Area();
        AreaDAO areaDAO = new AreaDAO();
        resultado.setId(idArea);
        resultado = areaDAO.select(resultado);

        areaDAO.closeConnection();

        return resultado;
    }
    
    public static int countPagesAreaDisabledCenter(String idCenter, int resultsInPage, String search) throws NamingException, SQLException {
        int paginas;
        int cantArea;
        AreaDAO areaDAO = new AreaDAO();
        cantArea = areaDAO.countAreasCenter(idCenter, search, false);
        areaDAO.closeConnection();

        paginas = cantArea / resultsInPage;
        if (cantArea % resultsInPage != 0) {
            paginas++;
        }

        return paginas;
    }
    
    public static ArrayList<Area> viewAreaDisabledCenter(String idCenter, int pagina, int cantXpag, String search) throws NamingException, SQLException {
        ArrayList<Area> area;
        AreaDAO areaDAO = new AreaDAO();
        area = areaDAO.selectSomeAreasCenter(idCenter, pagina, cantXpag, search, false);

        areaDAO.closeConnection();

        return area;
    }
    
    public static int countPagesProgramsCenter(String idCenter, int resultsInPage, String search) throws NamingException, SQLException{
        int result;
        
        int quantityProgrms;
        ProgramaDAO programaDAO = new ProgramaDAO();
        quantityProgrms = programaDAO.countProgramsCenter(idCenter, search, true);
        programaDAO.closeConnection();

        result = quantityProgrms / resultsInPage;
        if (quantityProgrms % resultsInPage != 0) {
            result++;
        }
        
        return result;
    }
    
    public static ArrayList<Programa> viewProgramsCenter(String idCenter, int page, int resultsInPage, String search) throws NamingException, SQLException {
        ArrayList<Programa> result;
        ProgramaDAO programaDAO = new ProgramaDAO();
        result = programaDAO.selectSomeProgramsCenter(idCenter, page, resultsInPage, search, true);

        programaDAO.closeConnection();

        return result;
    }
    
    public static int countPagesProgramsDisableCenter(String idCenter, int resultsInPage, String search) throws NamingException, SQLException{
        int result;
        
        int quantityPrograms;
        ProgramaDAO programaDAO = new ProgramaDAO();
        quantityPrograms = programaDAO.countProgramsCenter(idCenter, search, false);
        programaDAO.closeConnection();

        result = quantityPrograms / resultsInPage;
        if (quantityPrograms % resultsInPage != 0) {
            result++;
        }
        
        return result;
    }
    
    public static ArrayList<Programa> viewProgramsDisableCenter(String idCenter, int page, int resultsInPage, String search) throws NamingException, SQLException {
        ArrayList<Programa> result;
        ProgramaDAO programaDAO = new ProgramaDAO();
        result = programaDAO.selectSomeProgramsCenter(idCenter, page, resultsInPage, search, false);

        programaDAO.closeConnection();

        return result;
    }
    
    public static ArrayList<Area> viewAllAreasCenter(String idCenter) throws NamingException, SQLException{
        ArrayList<Area> result;
        AreaDAO areaDAO = new AreaDAO();
        result = areaDAO.selectAllCenter(idCenter);
        areaDAO.closeConnection();
        return result;
    }
    
    public static int registerProgram (Programa programa) throws NamingException, SQLException{
        int result = 0;
        
        programa.setActivo(true);
        
        ProgramaDAO programaDAO = new ProgramaDAO();
        if(programaDAO.Insert(programa)){
            result = 1;
        }
        programaDAO.closeConnection();
        
        return result;
    }
    
    public static int disableProgram(Programa programa) throws NamingException, SQLException{
        int result = 0;
        
        
        
        ProgramaDAO programaDAO = new ProgramaDAO();
        if(programaDAO.disable(programa)){
            result = 1;
        }
        programaDAO.closeConnection();
        
        return result;
    }
    
    public static int enableProgram(Programa programa) throws NamingException, SQLException {
        int resultado = 0;
        ProgramaDAO programaDAO = new ProgramaDAO();
        if(programaDAO.enable(programa)){
            resultado = 1;
        }
        return resultado;
    }
    
}
