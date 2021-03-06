/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.servlet.home.role.coordinator.functionary;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Business.Coordinator;
import model.bean.Funcionario;
import model.bean.Rol;

/**
 *
 * @author Juan David Segura Castro
 */
@WebServlet(urlPatterns = {"/home/role/coordinator/functionary/manage"})
public class Manage extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String idFuncionario = request.getParameter("id");
            int idFun;
            String opcion = request.getParameter("action");

            idFun = Integer.parseInt(idFuncionario);
            if (idFun <= 0) {
                request.setAttribute("messageType", "warning");
                request.setAttribute("message", "Para realizar la operación es necesario seleccionar uno de los funcionarios");
            } else {
                switch (opcion) {
                    case "disable":
                        HttpSession sesion = (HttpSession) ((HttpServletRequest) request).getSession();
                        String idCentro = ((Funcionario) sesion.getAttribute("usuario")).getCentro().getId();
                        switch (Coordinator.disableFunctionary(idFun, idCentro)) {
                            case 0:
                                request.setAttribute("messageType", "danger");
                                request.setAttribute("message", "El funcionario no ha podido ser inhabilitado");
                                break;
                            case 1:
                                request.setAttribute("messageType", "success");
                                request.setAttribute("message", "El funcionario fue inhabilitado exitosamente");
                                break;
                            case 2:
                                request.setAttribute("messageType", "danger");
                                request.setAttribute("message", "Solo queda un coordinador habilitado para el centro");
                                break;
                        }
                        break;
                    case "changeRoles":
                        Funcionario funcionarioResult = Coordinator.viewAllInfoFunctionary(idFun);
                        request.setAttribute("funcionario", funcionarioResult);
                        ArrayList<Rol> roles = Coordinator.viewRoles();
                        request.setAttribute("roles", roles);
                        request.getRequestDispatcher("/home/role/coordinator/functionary/modalRoles.jsp").forward(request, response);
                        return;
                    default:
                        request.setAttribute("messageType", "danger");
                        request.setAttribute("message", "no ha podido ser completada la accion");
                        break;
                }
            }
            request.getRequestDispatcher("/WEB-INF/model/message.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("mensaje", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
