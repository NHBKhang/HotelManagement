import { useState } from "react";

const BookingModal = ({ room, defaults, onClose, onSuccess }) => {
    const [payload, setPayload] = useState({ ...defaults });
    const [loading, setLoading] = useState(false);

    const submit = async () => {
        setLoading(true);
        try {
            onSuccess && onSuccess();
        } catch (err) {
        } finally { setLoading(false); }
    };

    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl p-6 w-full max-w-lg">
                <h3 className="text-xl font-bold">Đặt phòng: {room.name}</h3>

                <div className="mt-4 flex gap-3 justify-end">
                    <button onClick={onClose} className="px-4 py-2">Huỷ</button>
                    <button onClick={submit} className="px-4 py-2 bg-indigo-600 text-white" disabled={loading}>
                        {loading ? "Đang xử lý..." : "Xác nhận & Thanh toán"}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default BookingModal;