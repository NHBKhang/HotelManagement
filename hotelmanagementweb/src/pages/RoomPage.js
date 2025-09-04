import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import API, { endpoints } from "../configs/API";

const RoomPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [room, setRoom] = useState(null);
    const [payload, setPayload] = useState({});
    const [loading, setLoading] = useState(true);
    const [totalPrice, setTotalPrice] = useState(0);
    const [services, setServices] = useState([]);

    const updatePayload = (e) =>
        setPayload((p) => ({ ...p, [e.target.name]: e.target.value }));

    useEffect(() => {
        const loadRoom = async () => {
            try {
                const res = await API.get(endpoints.room(id));
                setRoom(res.data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        loadRoom();
    }, [id]);

    useEffect(() => {
        const loadServices = async () => {
            try {
                const res = await API.get(endpoints.services);
                setServices(res.data);
            } catch (err) {
            }
        };
        loadServices();
    }, []);

    useEffect(() => {
        if (room?.roomType?.pricePerNight && payload.checkin && payload.checkout) {
            const checkinDate = new Date(payload.checkin);
            const checkoutDate = new Date(payload.checkout);
            const nights = Math.max(
                (checkoutDate - checkinDate) / (1000 * 60 * 60 * 24), 0);

            let total = (nights + 1) * room.roomType.pricePerNight;

            // if (payload.services) {
            //     payload.services.forEach(s => {
            //         total += s.price * s.quantity;
            //     });
            // }

            setTotalPrice(Math.round(total));
        } else {
            setTotalPrice(0);
        }
    }, [payload.checkin, payload.checkout, payload.services, room]);

    if (loading) return <p className="p-6 text-center text-gray-500">Đang tải...</p>;
    if (!room) return <p className="p-6 text-center text-red-500">Không tìm thấy phòng.</p>;

    const roomType = room.roomType || {};
    const today = new Date().toISOString().split("T")[0];
    const minCheckout = payload.checkin || today;

    const onBook = (e) => {
        e.preventDefault();
        const bookingData = {
            room,
            checkin: payload.checkin,
            checkout: payload.checkout,
            guests: payload.guests || 1,
            total: totalPrice,
        };
        navigate("/payment", { state: { bookingData } });
    };

    return (
        <div className="max-w-7xl mx-auto px-6 xl:px-0 py-12 space-y-10">
            <div className="relative w-full h-80 md:h-96 rounded-xl overflow-hidden shadow-lg">
                <img
                    src={room.image || "/img/room-placeholder.jpg"}
                    alt={roomType.name || room.roomNumber}
                    className="w-full h-full object-cover"
                />
                <div className="absolute bottom-0 left-0 w-full bg-gradient-to-t from-black/70 to-transparent p-6 text-white">
                    <h1 className="text-3xl font-bold">Phòng {room.roomNumber} - {roomType.name}</h1>
                    <p className="mt-1 text-sm">Sức chứa tối đa: {roomType.maxGuests || 1} khách</p>
                    <p className="mt-1 text-sm">Diện tích: {room.size || "N/A"}</p>
                    <span className={`inline-block mt-2 px-3 py-1 text-sm font-medium rounded-full ${room.tailwindClass || "bg-gray-200 text-gray-800"}`}>
                        {room.statusDescription || room.status}
                    </span>
                </div>
            </div>

            <div className="grid lg:grid-cols-10 gap-10">
                <div className="lg:col-span-6 space-y-8 bg-white dark:bg-slate-900 rounded-xl shadow">
                    <section className="p-6 space-y-3">
                        <h2 className="text-2xl font-semibold border-b pb-2">Mô tả</h2>
                        <p className="text-slate-700 dark:text-slate-300">
                            {roomType.description || "Phòng thoải mái, đầy đủ tiện nghi."}
                        </p>
                    </section>

                    <section className="p-6 space-y-3">
                        <h2 className="text-2xl font-semibold border-b pb-2">Tiện nghi</h2>
                        <ul className="list-disc list-inside text-slate-700 dark:text-slate-300 space-y-1">
                            {roomType.amenities?.length > 0 ? (
                                roomType.amenities.map((a, idx) => <li key={idx}>{a}</li>)
                            ) : (
                                <li>Chưa có thông tin tiện nghi.</li>
                            )}
                        </ul>
                    </section>

                    <section className="p-6 space-y-3">
                        <h2 className="text-2xl font-semibold border-b pb-2">Chính sách</h2>
                        <p className="text-slate-700 dark:text-slate-300">
                            {roomType.policy || "Hủy miễn phí trước 24h."}
                        </p>
                    </section>
                </div>

                <div className="lg:col-span-4 bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-6">
                    <div className="text-center">
                        <span className="text-4xl font-bold text-indigo-600">
                            {roomType.pricePerNight?.toLocaleString()} VND
                        </span>
                        <p className="text-sm text-gray-500">/ đêm</p>
                    </div>

                    <form onSubmit={onBook} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium mb-1">Nhận phòng</label>
                            <input
                                name="checkin"
                                type="date"
                                min={today}
                                required
                                value={payload.checkin || ""}
                                onChange={updatePayload}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-400 focus:outline-none dark:text-black"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Trả phòng</label>
                            <input
                                name="checkout"
                                type="date"
                                min={minCheckout}
                                required
                                value={payload.checkout || ""}
                                onChange={updatePayload}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-400 focus:outline-none dark:text-black"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Số khách</label>
                            <input
                                name="guests"
                                type="number"
                                min="1"
                                max={roomType.maxGuests || 10}
                                step="1"
                                required
                                value={payload.guests || 1}
                                onChange={updatePayload}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-400 focus:outline-none dark:text-black"
                            />
                        </div>

                        <div>
                            <h3 className="block text-sm font-medium mb-2">Dịch vụ đi kèm</h3>
                            <div className="space-y-3 max-h-48 overflow-y-auto pr-1 custom-scroll">
                                {services.results?.map((s) => {
                                    const selected = payload.services?.find(item => item.id === s.id);
                                    return (
                                        <div
                                            key={s.id}
                                            className={`flex items-center justify-between p-3 rounded-lg border transition-colors duration-200 ${selected
                                                    ? "bg-indigo-50 dark:bg-slate-800 border-indigo-400"
                                                    : "bg-gray-50 dark:bg-slate-900 border-gray-200 dark:border-slate-700 hover:border-indigo-300"}`}
                                        >
                                            <label className="flex items-center gap-2 cursor-pointer">
                                                <input
                                                    type="checkbox"
                                                    checked={!!selected}
                                                    onChange={(e) => {
                                                        if (e.target.checked) {
                                                            setPayload((p) => ({
                                                                ...p,
                                                                services: [...(p.services || []), { ...s, quantity: 1 }]
                                                            }));
                                                        } else {
                                                            setPayload((p) => ({
                                                                ...p,
                                                                services: p.services.filter(item => item.id !== s.id)
                                                            }));
                                                        }
                                                    }}
                                                    className="w-4 h-4 accent-indigo-600"
                                                />
                                                <span className="text-sm font-medium">
                                                    {s.name}
                                                    <span className="text-gray-500 text-xs"> ({s.unit})</span>
                                                </span>
                                            </label>
                                            <div className="flex items-center gap-2">
                                                <span className="text-green-600 dark:text-green-400 text-sm font-semibold">
                                                    +{s.price.toLocaleString()} VND
                                                </span>
                                                {selected && s.allowQuantity && (
                                                    <input
                                                        type="number"
                                                        min="1"
                                                        value={selected.quantity}
                                                        onChange={(e) =>
                                                            setPayload((p) => ({
                                                                ...p,
                                                                services: p.services.map(item =>
                                                                    item.id === s.id
                                                                        ? { ...item, quantity: parseInt(e.target.value) || 1 }
                                                                        : item
                                                                )
                                                            }))
                                                        }
                                                        className="w-14 border rounded px-2 py-1 text-right text-sm dark:bg-slate-800 dark:border-slate-600"
                                                    />
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>

                        {totalPrice > 0 && (
                            <div className="p-3 bg-indigo-50 dark:bg-slate-800 rounded-lg text-center">
                                <p className="text-lg font-semibold text-indigo-700 dark:text-indigo-300">
                                    Tổng: {totalPrice.toLocaleString()} VND
                                </p>
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors duration-200"
                        >
                            Đặt phòng
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default RoomPage;
