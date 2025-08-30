import { useEffect, useState } from 'react';
import { CheckCircle } from 'lucide-react';
import { endpoints, useAuthAPI } from '../../configs/API';
import { useLocation, useNavigate } from 'react-router-dom';

const PaymentResultPopup = () => {
    const [visible, setVisible] = useState(false);
    const [transactionId, setTransactionId] = useState(null);
    const authAPI = useAuthAPI();
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const checkVnpayReturn = async () => {
            const query = location.search;
            const params = new URLSearchParams(query);
            if (query.includes("vnp_")) {
                try {
                    const res = await authAPI.get(endpoints["vnpay-return"] + query);
                    if (res.data.code === 1) {
                        setTransactionId(params.get("vnp_TransactionNo"));
                        setVisible(true);
                    }
                } catch (err) {
                    console.error("Lỗi kiểm tra thanh toán:", err);
                }
            }
        };
        checkVnpayReturn();
    }, [location.search, authAPI]);

    if (!visible) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
            <div className="bg-white dark:bg-gray-900 p-6 rounded-2xl shadow-lg max-w-xl text-center">
                <CheckCircle className="mx-auto mb-4 text-green-500 w-12 h-12" />
                <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
                    Thanh toán thành công!
                </h2>
                {transactionId && (
                    <p className="mt-2 text-gray-700 dark:text-gray-300">
                        Mã giao dịch: <strong>{transactionId}</strong>
                    </p>
                )}
                <p className="mt-2 text-gray-600 dark:text-gray-400">
                    Cảm ơn bạn đã tin tưởng! Hãy chuẩn bị cho kỳ nghỉ tuyệt vời thôi nào.
                </p>
                <button
                    onClick={() => {
                        setVisible(false);
                        navigate(location.pathname, { replace: true });
                    }}
                    className="mt-6 px-4 py-2 rounded-lg bg-green-600 text-white hover:bg-green-700"
                >
                    OK
                </button>
            </div>
        </div>
    );
};

export default PaymentResultPopup;
