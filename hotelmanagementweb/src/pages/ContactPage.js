import { motion } from "framer-motion";
import { Mail, Phone, MapPin, Send } from "lucide-react";
import { useState } from "react";
import { useNotification } from "../utils/toast";

const ContactPage = () => {
    const sendNotification = useNotification();
    const [form, setForm] = useState({ name: "", email: "", message: "" });

    const updateForm = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const onSubmit = (e) => {
        e.preventDefault();
        console.log("Dữ liệu gửi đi:", form);
        sendNotification({ message: "Cảm ơn bạn đã liên hệ!" }, "info");
        setForm({ name: "", email: "", message: "" });
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-cyan-50 dark:from-gray-900 dark:via-gray-950 dark:to-gray-900 px-6 py-12 flex items-center justify-center transition-colors duration-300">
            <div className="max-w-5xl w-full grid md:grid-cols-2 gap-10">
                <motion.div
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.6 }}
                    className="space-y-6"
                >
                    <h1 className="text-4xl font-extrabold text-gray-900 dark:text-white">
                        Liên hệ với chúng tôi
                    </h1>
                    <p className="text-gray-600 dark:text-gray-300">
                        Nếu bạn có thắc mắc hoặc cần hỗ trợ, hãy để lại thông tin
                        và chúng tôi sẽ phản hồi nhanh chóng.
                    </p>

                    <div className="space-y-4">
                        <div className="flex items-center gap-3">
                            <Mail className="text-indigo-500 w-5 h-5" />
                            <span className="text-gray-700 dark:text-gray-300">
                                support@hotelbooking.com
                            </span>
                        </div>
                        <div className="flex items-center gap-3">
                            <Phone className="text-indigo-500 w-5 h-5" />
                            <span className="text-gray-700 dark:text-gray-300">
                                +84 123 456 789
                            </span>
                        </div>
                        <div className="flex items-center gap-3">
                            <MapPin className="text-indigo-500 w-5 h-5" />
                            <span className="text-gray-700 dark:text-gray-300">
                                123 Đường ABC, Quận 1, TP. HCM
                            </span>
                        </div>
                    </div>
                </motion.div>

                <motion.form
                    onSubmit={onSubmit}
                    initial={{ opacity: 0, x: 20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.6 }}
                    className="bg-white dark:bg-gray-800 shadow-xl rounded-2xl p-6 border border-gray-200 dark:border-gray-700 space-y-4"
                >
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                            Họ và tên
                        </label>
                        <input
                            type="text"
                            name="name"
                            value={form.name}
                            onChange={updateForm}
                            required
                            className="mt-1 w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                            Email
                        </label>
                        <input
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={updateForm}
                            required
                            className="mt-1 w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                            Tin nhắn
                        </label>
                        <textarea
                            name="message"
                            value={form.message}
                            onChange={updateForm}
                            required
                            rows="4"
                            className="mt-1 w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 rounded-xl shadow-md transition-colors"
                    >
                        <Send className="w-4 h-4" />
                        Gửi liên hệ
                    </button>
                </motion.form>
            </div>
        </div>
    );
};

export default ContactPage;
