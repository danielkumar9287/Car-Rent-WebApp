package com.mv.schelokov.car_rent.actions.admin;

import com.mv.schelokov.car_rent.actions.AbstractAction;
import com.mv.schelokov.car_rent.actions.JspForward;
import com.mv.schelokov.car_rent.exceptions.ActionException;
import com.mv.schelokov.car_rent.model.entity.Car;
import com.mv.schelokov.car_rent.model.entity.RentOrder;
import com.mv.schelokov.car_rent.model.services.CarService;
import com.mv.schelokov.car_rent.model.services.OrderService;
import com.mv.schelokov.car_rent.model.services.exceptions.ServiceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class CloseOrder extends AbstractAction {
    
    private static final Logger LOG = Logger.getLogger(ApproveOrder.class);
    private static final String ERROR = "Failed to close order";

    @Override
    public JspForward execute(HttpServletRequest req, HttpServletResponse res)
            throws ActionException {
        
        JspForward forward = new JspForward();
        if (isAdmin(req)) {
            int invoiceId = getIntParam(req, "id");
            if (invoiceId < 1) {
                throw new ActionException("Wrong id parameter for invoice entity");
            }
            try {
                RentOrder order = (RentOrder) OrderService.getOrderById(invoiceId);
                
                Car car = CarService.getCarById(order.getCar().getId());
                car.setAvailable(true);
                CarService.updateCar(car);
                
                forward.setUrl("action/opened_orders");
                forward.setRedirect(true);
                        
                return forward;
            } catch (ServiceException ex) {
                LOG.error(ERROR, ex);
                throw new ActionException(ERROR, ex);
            }
        } else {
            sendForbidden(res);
            return forward;
        }
    }
}