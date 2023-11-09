import org.loushang.next.dao.Column;
import org.loushang.next.dao.Table;
import org.loushang.next.data.StatefulDatabean;



/**
* @Author 系统自动生成
* @Date 2023-11-09
*/
@Table(tableName = "DIRECT_SALE_ORDER_LOG",keyFields ="UUID")
public class DirectSaleOrderLog extends StatefulDatabean{

	/**
	* 唯一内码
	*/
	@Column(name = "UUID")
	private String uuid;

	/**
	* 订单编号
	*/
	@Column(name = "ORDER_ID")
	private String orderId;

	/**
	* 查询环节1 申请查询 2 申请提交 3 放款提交
	*/
	@Column(name = "QUERY_NODE")
	private String queryNode;

	/**
	* 查询时间
	*/
	@Column(name = "QUERY_TIME")
	private String queryTime;

	/**
	* 操作人
	*/
	@Column(name = "OPER")
	private String oper;

	/**
	* 返回的json串
	*/
	@Column(name = "LOG_CONTNET")
	private String logContnet;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getQueryNode() {
		return queryNode;
	}

	public void setQueryNode(String queryNode) {
		this.queryNode = queryNode;
	}

	public String getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getLogContnet() {
		return logContnet;
	}

	public void setLogContnet(String logContnet) {
		this.logContnet = logContnet;
	}

}