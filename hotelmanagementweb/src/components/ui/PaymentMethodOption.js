const PaymentMethodOption = ({
    value,
    label,
    icon,
    paymentMethod,
    setPaymentMethod,
    disabled = false,
    children,
}) => {
    return (
        <div>
            <label className="flex items-center space-x-3 cursor-pointer">
                <input
                    type="radio"
                    name="payment"
                    value={value}
                    checked={paymentMethod === value}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    className="w-4 h-4 text-indigo-600 focus:ring-indigo-500"
                    disabled={disabled}
                />
                {icon && (
                    <img
                        src={icon}
                        alt={label}
                        className="w-8 h-8 object-contain"
                    />
                )}
                <span>{label}</span>
            </label>
            {paymentMethod === value && <div className="ml-7 mt-4">{children}</div>}
        </div>
    );
};

export default PaymentMethodOption;
