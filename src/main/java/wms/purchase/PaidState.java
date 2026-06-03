package wms.purchase;

public class PaidState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "已付款";
    }
}
