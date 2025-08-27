import { useNavigate } from "react-router-dom";

const RoomCard = ({ room }) => {
    const navigate = useNavigate();
    return (
        <div className="border rounded-xl p-4 bg-white shadow">
            <img src={room.image || "/img/room-placeholder.jpg"} alt={room.name} className="w-full h-40 object-cover rounded-md mb-3" />
            <h3 className="font-semibold">{room.name}</h3>
            <p className="text-sm text-slate-500">{room.description}</p>
            <div className="flex items-center justify-between mt-3">
                <div>
                    <div className="text-lg font-bold">{room.price?.toLocaleString()} VNĐ</div>
                    <div className="text-xs text-slate-500">{room.type}</div>
                </div>
                <button onClick={() => navigate(`/rooms/${room.id}`)} className="px-4 py-2 bg-indigo-600 text-white rounded-md">Chi tiết</button>
            </div>
        </div>
    );
}

export default RoomCard;