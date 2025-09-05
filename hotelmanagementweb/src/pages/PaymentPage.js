import { useState } from "react";
import { useLocation } from "react-router-dom";
import { endpoints, useAuthAPI } from "../configs/API";
import { useUserContext } from "../configs/UserContext";
import PaymentMethodOption from "../components/ui/PaymentMethodOption";

const banks = [
    { code: "", name: "-- Chọn ngân hàng --" },
    { code: "NCB", name: "Ngân hàng Quốc Dân (NCB)" },
    { code: "VCB", name: "Vietcombank" },
    { code: "BIDV", name: "BIDV" },
    { code: "VIB", name: "VIB" },
    { code: "TCB", name: "Techcombank" },
    { code: "VNPAY-QR", name: "VNPAY QR" },
];

const locales = [
    { code: "vn", name: "Tiếng Việt" },
    { code: "en", name: "English" }
];

const paymentOptions = [
    {
        value: "vnpay",
        label: "Thanh toán qua VNPAY",
        icon: "https://vnpay.vn/s1/statics.vnpay.vn/2023/9/06ncktiwd6dc1694418196384.png",
        render: ({ body, updateBody }) => (
            <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="flex flex-col space-y-1">
                        <label className="text-sm font-medium mb-1">Ngân hàng:</label>
                        <select
                            value={body.vnpay.bankCode}
                            onChange={(e) => updateBody("vnpay", "bankCode", e.target.value)}
                            className="w-full border rounded-lg px-3 py-2 focus:ring focus:ring-indigo-200 dark:text-black"
                        >
                            {banks.map((bank) => (
                                <option key={bank.code} value={bank.code}>
                                    {bank.name}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div className="flex flex-col space-y-1">
                        <label className="text-sm font-medium mb-1">Ngôn ngữ:</label>
                        <select
                            value={body.vnpay.locale}
                            onChange={(e) => updateBody("vnpay", "locale", e.target.value)}
                            className="w-full border rounded-lg px-3 py-2 focus:ring focus:ring-indigo-200 dark:text-black"
                        >
                            {locales.map((l) => (
                                <option key={l.code} value={l.code}>
                                    {l.name}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
                <div className="flex flex-col space-y-1">
                    <label className="text-sm font-medium mb-1">Ghi chú:</label>
                    <textarea
                        value={body.vnpay.notes}
                        onChange={(e) => updateBody("vnpay", "notes", e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 focus:ring focus:ring-indigo-200 dark:text-black"
                        rows={3}
                    />
                </div>
                <p className="text-sm text-gray-500">
                    Bạn sẽ được chuyển đến cổng thanh toán VNPAY để hoàn tất giao dịch.
                </p>
            </div>
        )
    },
    {
        value: "credit-card",
        label: "Thẻ tín dụng / ghi nợ",
        icon: "https://img.icons8.com/color/48/000000/bank-card-back-side.png",
        disabled: true,
        render: () => (<></>)
    },
    {
        value: "bank-transfer",
        label: "Chuyển khoản ngân hàng",
        icon: "https://img.icons8.com/color/48/000000/bank-building.png",
        disabled: true,
        render: () => <></>
    },
    {
        value: "cash",
        label: "Thanh toán khi nhận phòng",
        icon: "https://img.icons8.com/color/48/000000/cash-in-hand.png",
        disabled: true,
        render: () => (
            <p className="text-sm text-gray-500">
                Bạn sẽ thanh toán trực tiếp khi nhận phòng.
            </p>
        )
    }
];

const PaymentPage = () => {
    const { state } = useUserContext();
    const user = state?.currentUser;

    const location = useLocation();
    const authAPI = useAuthAPI();

    const bookingData = location.state?.bookingData || {};
    const room = bookingData.room || {};

    const [paymentMethod, setPaymentMethod] = useState("vnpay");
    const [body, setBody] = useState({
        vnpay: {
            bankCode: "NCB",
            locale: "vn",
            notes: ""
        },
        transfer: {
            file: null
        }
    });

    const updateBody = (method, field, value) => {
        setBody(prev => ({
            ...prev,
            [method]: { ...prev[method], [field]: value }
        }));
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        try {
            const bookingRes = await authAPI.post(endpoints.bookings, {
                checkin: bookingData.checkin,
                checkout: bookingData.checkout,
                guests: bookingData.guests,
                services: bookingData.services.map(s => ({
                    id: s.id,
                    quantity: s.quantity
                })),
                roomId: room.id
            });

            let res = null;
            console.info(bookingRes.data)
            if (paymentMethod === 'vnpay') {
                res = await authAPI.post(endpoints['vnpay-payment'], {
                    itemId: room.id,
                    itemType: 'room',
                    bookingId: bookingRes.data.id,
                    returnUrl: `${window.location.origin}/my-bookings/${(bookingRes.data.id)}`,
                    amount: bookingData.total,
                    orderType: "other",
                    bankCode: body.vnpay.bankCode,
                    locale: body.vnpay.locale,
                    notes: body.vnpay.notes
                });
            }

            if (res.data && res.data.payUrl) {
                window.location.href = res.data.payUrl;
            }
        } catch (err) {
            console.error(err);
        }
    };

    if (!room.id) {
        return (
            <p className="p-6 text-center text-red-500">
                Không có thông tin phòng để thanh toán.
            </p>
        );
    }

    return (
        <div className="max-w-6xl mx-auto px-6 py-10">
            <div className="grid grid-cols-1 md:grid-cols-10 gap-8">
                <div className="md:col-span-3 space-y-8">
                    <div className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-4">
                        <h2 className="text-2xl font-bold">Chi tiết đặt phòng</h2>
                        <p>
                            <strong>Phòng:</strong> {room.roomNumber} - {room.roomType?.name}
                        </p>
                        <p>
                            <strong>Ngày nhận:</strong> {bookingData.checkin}
                        </p>
                        <p>
                            <strong>Ngày trả:</strong> {bookingData.checkout}
                        </p>
                        <p>
                            <strong>Số khách:</strong> {bookingData.guests}
                        </p>
                        <p className="text-lg font-semibold text-indigo-600">
                            Tổng: {bookingData.total?.toLocaleString()}₫
                        </p>
                    </div>

                    <div className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-3">
                        <h2 className="text-2xl font-bold">Khách hàng</h2>
                        <p>
                            <strong>Họ và tên:</strong> {user?.firstName} {user?.lastName}
                        </p>
                        <p>
                            <strong>Email:</strong> {user?.email}
                        </p>
                        <p>
                            <strong>Số điện thoại:</strong> {user?.phone || "Chưa cập nhật"}
                        </p>
                    </div>
                </div>

                <form
                    onSubmit={onSubmit}
                    className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-6 md:col-span-7"
                >
                    <h2 className="text-2xl font-bold">Thanh toán</h2>

                    <div className="space-y-4">
                        {paymentOptions.map((opt) => (
                            <PaymentMethodOption
                                key={opt.value}
                                value={opt.value}
                                label={opt.label}
                                icon={opt.icon}
                                paymentMethod={paymentMethod}
                                setPaymentMethod={setPaymentMethod}
                                disabled={opt.disabled}
                            >
                                {opt.render({ body, updateBody })}
                            </PaymentMethodOption>
                        ))}
                    </div>

                    <div className="text-center">
                        <button
                            type="submit"
                            className="px-6 bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition"
                        >
                            Xác nhận & Thanh toán
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default PaymentPage;
