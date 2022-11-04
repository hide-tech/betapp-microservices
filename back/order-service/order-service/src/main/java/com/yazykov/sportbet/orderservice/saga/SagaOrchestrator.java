package com.yazykov.sportbet.orderservice.saga;

import com.yazykov.sportbet.orderservice.domain.OrderDetail;
import com.yazykov.sportbet.orderservice.dto.event.*;
import lombok.*;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final SagaManager sagaManager;

    @SqsListener(value = "OrderQueue")
    public void processMessage(Object obj){
        if (obj instanceof CustomerNotRegister){
            CustomerNotRegister cnr = (CustomerNotRegister) obj;
            sagaManager.processCustomerNotRegisterEvent(cnr);
        } else if (obj instanceof CustomerPaymentInfo) {
            CustomerPaymentInfo cpi = (CustomerPaymentInfo) obj;
            sagaManager.processCustomerPaymentInfo(cpi);
        } else if (obj instanceof FaultResponse) {
            FaultResponse fr = (FaultResponse) obj;
            sagaManager.processFaultResponse(fr);
        } else if (obj instanceof FaultResponseOdd) {
            FaultResponseOdd fro = (FaultResponseOdd) obj;
            sagaManager.processFaultResponseOdd(fro);
        } else if (obj instanceof OrderFinish) {
            OrderFinish of = (OrderFinish) obj;
            sagaManager.processOrderFinished(of);
        } else if (obj instanceof SuccessfulResponse) {
            SuccessfulResponse sr = (SuccessfulResponse) obj;
            sagaManager.processSuccessfulResponse(sr);
        } else if (obj instanceof SuccessResponseOdd) {
            SuccessResponseOdd sro = (SuccessResponseOdd) obj;
            sagaManager.processSuccessfulOddResponse(sro);
        } else if (obj instanceof SuccessResponseResult) {
            SuccessResponseResult srr = (SuccessResponseResult) obj;
            sagaManager.processSuccessResponseResult(srr);
        }
    }

    public void sagaStart(OrderDetail orderDetail){
        sagaManager.startOrderSaga(orderDetail);
    }
}
