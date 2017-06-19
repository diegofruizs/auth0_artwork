/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.artwork.exceptions;

import co.edu.uniandes.csw.artwork.exceptions.BusinessLogicException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Asistente
 */
@Provider
public class BusinessLogicExceptionMapper implements ExceptionMapper<BusinessLogicException>{

    @Override
    public Response toResponse(BusinessLogicException exception) {
        
        return Response.status(Response.Status.PRECONDITION_FAILED)
                .entity(getInitCause(exception).getLocalizedMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build(); //To change body of generated methods, choose Tools | Templates.
    }
     private Throwable getInitCause(Throwable e) {
        if (e.getCause() != null) {
            return getInitCause(e.getCause());
        } else {
            return e;
        }
    }
}
