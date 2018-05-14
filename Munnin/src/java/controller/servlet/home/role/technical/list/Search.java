/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.servlet.home.role.technical.list;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Business.Technical;
import model.bean.Funcionario;

/**
 *
 * @author Monica
 */
@WebServlet(urlPatterns = {"/home/role/technical/list/search"})
public class Search extends HttpServlet {

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

            String search = request.getParameter("search");
            String strPage = request.getParameter("page");
            int page = 1;
            if (strPage != null) {
                page = Integer.parseInt(strPage);
            }

            int cantXpag = 10;

            HttpSession sesion = (HttpSession) ((HttpServletRequest) request).getSession();
            Funcionario funcionario = (Funcionario) sesion.getAttribute("usuario");

            int totalPages = Technical.countPagesCheckList(funcionario.getId(), cantXpag, search);
            request.setAttribute("page", page);
            request.setAttribute("pages", util.Pager.showLinkedPages(page, totalPages, cantXpag));
            request.setAttribute("contentTable", Technical.viewCheckListFunctionary(funcionario.getId(), page, cantXpag, search));
            request.setAttribute("lastSearch", util.Pager.getSearchParameters(request));
            request.setAttribute("displayResult", "fulltable");
            request.setAttribute("idTable", "tableBodyList");
            request.setAttribute("urlServlet", (request.getContextPath()+"/home/role/pedagogical/list/search"));
            request.getRequestDispatcher("/home/role/pedagogical/list/table.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("mensaje", ex);
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