package wms.purchase;

public class InboundCompletedState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "已入库";
    }

    @Override
    public void generateInvoice(PurchaseOrder order) {
        order.setInvoiceNo(BusinessDocumentFactory.createInvoiceNo(order.getOrderNo()));
        order.changeState(new InvoicedState(), "生成发票");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        order.setReturnOrderNo(BusinessDocumentFactory.createReturnOrderNo(order.getOrderNo()));
        order.changeState(new ReturnedCompletedState(), "退货退款");
    }
}
