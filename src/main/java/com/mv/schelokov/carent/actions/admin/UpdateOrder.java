package com.mv.schelokov.carent.actions.admin;

import com.mv.schelokov.carent.actions.interfaces.AbstractAction;
import com.mv.schelokov.carent.actions.JspForward;
import com.mv.schelokov.carent.actions.consts.Jsps;
import com.mv.schelokov.carent.actions.exceptions.ActionException;
import com.mv.schelokov.carent.model.entity.RentOrder;
import com.mv.schelokov.carent.model.services.CarService;
import com.mv.schelokov.carent.model.services.OrderService;
import com.mv.schelokov.carent.model.services.exceptions.ServiceException;
import com.mv.schelokov.carent.model.validators.RentOrderValidator;
import com.mv.schelokov.carent.model.validators.ValidationResult;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class UpdateOrder extends AbstractAction {
    
    private static final Logger LOG = Logger.getLogger(UpdateOrder.class);
    private static final String ERROR = "Failed to update the rent order";
    private static final String WRONG_ID = "Wrong id parameter for order entity";
    private static final String DATE_PARSE_ERROR = "Date parse error!";
    private static final SimpleDateFormat FORMAT
            = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public JspForward execute(HttpServletRequest req, HttpServletResponse res)
            throws ActionException {
        
        JspForward forward = new JspForward();
        
        if (isAdmin(req)) {

            int orderId = getIntParam(req, "id");
            if (orderId < 1)
                throw new ActionException(WRONG_ID);
            try {
                OrderService orderService = OrderService.getInstance();
                RentOrder order = orderService.getOrderById(orderId);
                order.getCar().setId(getIntParam(req, "selected_car"));

                int validationResult = ValidationResult.OK;

                try {
                    order.setStartDate(FORMAT.parse(req.getParameter("start_date")));
                    order.setEndDate(FORMAT.parse(req.getParameter("end_date")));
                } catch (ParseException ex) {
                    LOG.error(DATE_PARSE_ERROR, ex);
                    validationResult = ValidationResult.INVALID_DATE;
                }
                
                if (validationResult == ValidationResult.OK)
                    validationResult = new RentOrderValidator(order).validate();
                if (validationResult == ValidationResult.OK) {
                    orderService.updateOrder(order);
                    
                    forward.setUrl("action/order_list");
                    forward.setRedirect(true);
                    
                    return forward;
                }
                
                CarService carService = CarService.getInstance();

                req.setAttribute("errParam", validationResult);
                req.setAttribute("car_list", carService.getAvailableCars());
                req.setAttribute("order", order);
                req.setAttribute("start_date",
                        FORMAT.format(order.getStartDate()));
                req.setAttribute("end_date", FORMAT.format(order.getEndDate()));
                req.setAttribute("action", String.format("update_order?id=%d",
                        order.getId()));
                
                forward.setUrl(Jsps.USER_SELECT_CAR);
                
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
