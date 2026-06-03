package wms.purchase;

public class InvoicedState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "已开票";
    }

    @Override
    public void pay(PurchaseOrder order) {
        order.changeState(new PaidState(), "付款");
    }
}
