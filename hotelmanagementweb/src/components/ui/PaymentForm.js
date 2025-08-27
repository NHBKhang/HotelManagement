const PaymentForm = ({ amount, onSuccess }) => {
    const handleSubmit = async (e) => {
        e.preventDefault();
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-3">
            <button className="w-full py-2 bg-indigo-600 text-white rounded">Thanh toán {amount?.toLocaleString()} VNĐ</button>
        </form>
    );
}

export default PaymentForm;