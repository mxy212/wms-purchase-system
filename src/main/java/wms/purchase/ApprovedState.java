package wms.purchase;

public class ApprovedState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "已审批";
    }

    @Override
    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState(), "取消采购单");
    }

    @Override
    public void generateInboundOrder(PurchaseOrder order) {
        order.setInboundOrderNo(BusinessDocumentFactory.createInboundOrderNo(order.getOrderNo()));
        order.changeState(new InboundCompletedState(), "生成入库单");
    }
}
