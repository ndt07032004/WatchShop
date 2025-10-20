<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<body>
<form method="post" action="<c:url value='/payment/momo'/>">
    <input type="hidden" name="amount" value="${totalAmount}"/>
    <input type="hidden" name="orderId" value="${orderId}"/>
    <button class="btn btn-warning">Thanh toán bằng MoMo</button>
</form>
</body>
</html>