package wms.purchase;

public class ReturnedCompletedState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "已退货完结";
    }
}
