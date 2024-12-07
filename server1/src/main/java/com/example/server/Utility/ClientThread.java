package com.example.server.Utility;

import com.example.server.Enums.RequestType;
import com.example.server.Models.Entities.*;
import com.example.server.Models.TCP.HibernateProxyTypeAdapter;
import com.example.server.Models.TCP.Request;
import com.example.server.Models.TCP.Response;
import com.example.server.Models.TCP.RoleSerializer;
import com.example.server.Services.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.hibernate.Hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.math.MathContext;

public class ClientThread implements Runnable {
    public DepreciationCalculationService depreciationCalculationService;
    private Socket clientsSocket;
    public PrintWriter writer;
    public BufferedReader reader;
    public Gson gson;
    private Response response;
    private Request request;
    private User currentUser;
    private UserService userService = new UserService();
    private RoleService roleService = new RoleService();
    public FixedAssetService fixedAssetService = new FixedAssetService();

    private ReportService reportService = new ReportService();

    public ClientThread(FixedAssetService fixedAssetService, DepreciationCalculationService depreciationCalculationService) {
        this.fixedAssetService = fixedAssetService;
        this.depreciationCalculationService = depreciationCalculationService;
    }
    public ClientThread(Socket socket) throws IOException {
        this.clientsSocket = socket;
        request = new Request();
        gson = new Gson();

        response = new Response();
        writer = new PrintWriter(clientsSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(clientsSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while(clientsSocket.isConnected()) {
                String line = reader.readLine();
                if (line == null) break;

                request = gson.fromJson(line, Request.class);
                System.out.println("Полученное сообщение: " + request.getMessage());
                request.setRequestType(RequestType.valueOf(request.getRequestType().name()));

                switch (request.getRequestType()) {
                    case REGISTER:
                        handleRegistration(request);
                        break;
                    case LOGIN:
                        handleLogin(request);
                        break;
                    case ADD_ASSET:
                        handleAddAsset(request);
                        break;
                    case DELETE_ASSET:
                        handleDeleteAsset(request);
                        break;
                    case GET_ASSET:
                        handleGetAsset(request);
                        break;
                    case GETALL_ASSET:
                        handleGetAllAssets();
                        break;
                    case UPDATE_ASSET:
                        handleUpdateAsset(request);
                        break;
                    case SEARCH_ASSET:
                        handleSearchAsset(request);
                        break;
                    case SORT_ASSET:
                        handleSortAssetRequest(request);
                        break;
                    case CALCULATE_AMORTIZATION:
                        handleCalculateAmortizationRequest(request);
                        break;
                    case GETALL_AMORTIZATION:
                        handleGetAllAmortization();
                        break;
                    case GETALL_USERS:
                        handleGetAllUsers();
                        break;
                    case GET_USER:
                        handleGetUser(request);
                        break;
                    case DELETE_USER:
                        handleDeleteUser(request);
                        break;
                    case UPDATE_USER:
                        handleUpdateUser(request);
                        break;
                    case SAVE_REPORT:
                        handleSaveReport(request);
                        break;
                    case SEND_MESSAGE:
                        handleSendMessage(request);
                        break;
                    case GET_MESSAGES:
                        handleGetMessages(request);
                        break;
                    case MARK_AS_READ:
                        handleMarkAsRead(request);
                        break;
                    // Добавьте другие обработчики запросов для ролей и пользователей здесь
                    default:
                        response.setMessage("Ошибка: Неизвестный тип запроса.");
                        writer.println(gson.toJson(response));
                        writer.flush();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка в потоке клиента: " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientsSocket != null) clientsSocket.close();
                System.out.println("Соединение с клиентом закрыто.");
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    private void handleSaveReport(Request request) {
        Response response = new Response();

        try {
            JsonObject requestParams = gson.fromJson(request.getMessage(), JsonObject.class);
            String reportType = requestParams.get("report_type").getAsString();
            String content = requestParams.get("content").toString();
            int userId = requestParams.get("created_by_user_id").getAsInt();
            String base64ImageData = requestParams.has("image_data") ? requestParams.get("image_data").getAsString() : null;

            byte[] imageData = base64ImageData != null ? Base64.getDecoder().decode(base64ImageData) : null;

            User user = userService.findById(userId);
            if (user == null) {
                response.setSuccess(false);
                response.setMessage("Пользователь с ID " + userId + " не найден.");
            } else {
                Report report = new Report(reportType, content, user, imageData);
                reportService.insert(report);

                response.setSuccess(true);
                response.setMessage("Отчет успешно сохранен.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Ошибка при сохранении отчета: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }


    private void handleRegistration(Request request) {
        System.out.println("Полученное сообщение: " + request.getMessage());
        User user = gson.fromJson(request.getMessage(), User.class);
        System.out.println("Полученный клиент:  " + user);
        Role role = user.getRole();

        if (UserService.isLoginExists(user.getLogin())) {
            response.setMessage("Ошибка: Логин уже занят.");

        } else {
            roleService.insert(role);
            userService.insert(user);
            response.setMessage("Регистрация прошла успешно.");

        }
        System.out.println("Sending response: " + gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }

    public void handleLogin(Request request) {
        Response response = new Response();
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(Role.class, new RoleSerializer())
                .create();
        try {

            User user = gson1.fromJson(request.getMessage(), User.class);
            System.out.println("Попытка входа для пользователя: " + user.getLogin());

            User foundUser = userService.login(user.getLogin(), user.getPassword());

            if (foundUser != null) {
                response.setMessage("Успешный вход для пользователя: " + foundUser.getLogin());
                response.setSuccess(true);
                response.setResponseType(RequestType.LOGIN);
                response.setRole(foundUser.getRole().getName()); // Add this line to include the role
                response.setUser(foundUser);
            } else {
                response.setMessage("Ошибка: Неверный логин или пароль.");
                response.setSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
        }
        System.out.println(gson1.toJson(response));
        writer.println(gson1.toJson(response));
        writer.flush();
    }
    private void handleAddAsset(Request request) {
        FixedAsset asset = gson.fromJson(request.getMessage(), FixedAsset.class);

        System.out.println("До сохранения: id = " + asset.getId());

        fixedAssetService.insert(asset);

        System.out.println("После сохранения: id = " + asset.getId());
        response.setId(asset.getId());
        response.setMessage("Актив успешно добавлен.");
        response.setSuccess(true);
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleDeleteAsset(Request request) {
        int assetId = Integer.parseInt(request.getMessage());
        fixedAssetService.delete(assetId);
        response.setMessage("Актив успешно удален.");
        response.setSuccess(true);
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetAsset(Request request) {
        int assetId = Integer.parseInt(request.getMessage());
        FixedAsset asset = fixedAssetService.findById(assetId);
        if (asset != null) {
            response.setMessage(gson.toJson(asset));
            response.setSuccess(true);
        } else {
            response.setMessage("Актив не найден.");
            response.setSuccess(false);
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetAllAssets() {
        List<FixedAsset> assets = fixedAssetService.findAll();
        response.setMessage(gson.toJson(assets));
        response.setSuccess(true);
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleUpdateAsset(Request request) {
        FixedAsset asset = gson.fromJson(request.getMessage(), FixedAsset.class);
        fixedAssetService.update(asset);
        response.setMessage("Актив успешно обновлен.");
        response.setSuccess(true);
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleSearchAsset(Request request) {
        JsonObject searchJson = gson.fromJson(request.getMessage(), JsonObject.class);
        String parameter = searchJson.get("parameter").getAsString();
        String value = searchJson.get("value").getAsString();

        List<FixedAsset> assets = fixedAssetService.searchAssets(parameter, value);

        if (assets != null) {
            response.setMessage(gson.toJson(assets));
            response.setSuccess(true);
        } else {
            response.setMessage("Ошибка поиска активов.");
            response.setSuccess(false);
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleSortAssetRequest(Request request) {
        Response response = new Response();

        try {
            JsonObject sortParams = gson.fromJson(request.getMessage(), JsonObject.class);
            String sortBy = sortParams.get("parameter").getAsString();
            String sortOrder = sortParams.get("direction").getAsString();

            List<FixedAsset> sortedAssets = fixedAssetService.sortAssets(sortBy, sortOrder);

            response.setSuccess(true);
            response.setMessage(gson.toJson(sortedAssets));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Ошибка при сортировке активов: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.println(gson.toJson(response));
                writer.flush();
            }
        }
    }



    public void handleCalculateAmortizationRequest(Request request) {
        Response response = new Response();
        DepreciationCalculationService calculationService = new DepreciationCalculationService();

        try {
            JsonObject requestParams = gson.fromJson(request.getMessage(), JsonObject.class);
            int assetId = requestParams.get("assetId").getAsInt();
            String method = requestParams.get("method").getAsString();

            List<? extends DepreciationCalculation> amortizationResults = null;

            BigDecimal depreciationRate = null;
            if ("Метод остаточной стоимости".equals(method) && requestParams.has("depreciationRate")) {
                try {
                    depreciationRate = new BigDecimal(requestParams.get("depreciationRate").getAsString());
                } catch (NumberFormatException e) {
                    response.setSuccess(false);
                    response.setMessage("Неверный формат ставки амортизации.");
                    writer.println(gson.toJson(response));
                    writer.flush();
                    return;
                }
            }

            switch (method) {
                case "Линейный":
                    amortizationResults = calculateStraightLineDepreciation(assetId);
                    break;
                case "Ускоренный":
                    amortizationResults = calculateAcceleratedDepreciation(assetId);
                    break;
                case "Метод остаточной стоимости":
                    if (depreciationRate != null) {
                        amortizationResults = calculateDecliningBalanceDepreciation(assetId, depreciationRate);
                    } else {
                        response.setSuccess(false);
                        response.setMessage("Не была передана ставка амортизации.");
                        break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный метод амортизации: " + method);
            }

            if (amortizationResults != null) {
                for (DepreciationCalculation calculation : amortizationResults) {
                    calculationService.insert(calculation);
                }

                response.setSuccess(true);
                response.setMessage(gson.toJson(amortizationResults));
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Ошибка при расчете амортизации: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }


    public List<StraightLineDepreciation> calculateStraightLineDepreciation(int assetId) {
        FixedAsset asset = fixedAssetService.findById(assetId);
        List<StraightLineDepreciation> results = new ArrayList<>();

        BigDecimal annualDepreciation = (asset.getInitialCost().subtract(asset.getResidualValue()))
                .divide(BigDecimal.valueOf(asset.getUsefulLife()), BigDecimal.ROUND_HALF_UP);
        LocalDate purchaseDate = LocalDate.parse(asset.getPurchaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (int year = 1; year <= asset.getUsefulLife(); year++) {
            StraightLineDepreciation calculation = new StraightLineDepreciation();
            calculation.setFixedAsset(asset);
            calculation.setCalculationDate(purchaseDate.plusYears(year).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            calculation.setDepreciationAmount(annualDepreciation);
            calculation.setAccumulatedDepreciation(annualDepreciation.multiply(BigDecimal.valueOf(year)));
            calculation.setDepreciationMethod("Линейный");
            calculation.setInitialCost(asset.getInitialCost());
            calculation.setResidualValue(asset.getInitialCost().subtract(calculation.getAccumulatedDepreciation()));

            results.add(calculation);
        }
        return results;
    }

    public List<AcceleratedDepreciation> calculateAcceleratedDepreciation(int assetId) {
        FixedAsset asset = fixedAssetService.findById(assetId);
        List<AcceleratedDepreciation> results = new ArrayList<>();
        BigDecimal depreciationRate = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(asset.getUsefulLife()), new MathContext(10));
        BigDecimal remainingValue = asset.getInitialCost();
        BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

        for (int year = 1; year <= asset.getUsefulLife(); year++) {
            BigDecimal annualDepreciation = remainingValue.multiply(depreciationRate).setScale(2, BigDecimal.ROUND_DOWN);

            if (remainingValue.subtract(annualDepreciation).compareTo(asset.getResidualValue()) < 0) {
                annualDepreciation = remainingValue.subtract(asset.getResidualValue());
            }

            AcceleratedDepreciation calculation = new AcceleratedDepreciation();
            calculation.setFixedAsset(asset);
            calculation.setCalculationDate(getCalculationDate(asset.getPurchaseDate(), year));
            calculation.setDepreciationAmount(annualDepreciation);
            accumulatedDepreciation = accumulatedDepreciation.add(annualDepreciation);
            calculation.setAccumulatedDepreciation(accumulatedDepreciation);
            calculation.setDepreciationMethod("Ускоренный");
            calculation.setInitialCost(asset.getInitialCost());
            calculation.setResidualValue(asset.getInitialCost().subtract(accumulatedDepreciation));
            calculation.setDepreciationRate(depreciationRate);

            results.add(calculation);
            remainingValue = calculation.getResidualValue();
        }
        return results;
    }

    public List<DecliningBalanceDepreciation> calculateDecliningBalanceDepreciation(int assetId, BigDecimal depreciationRate) {
        FixedAsset asset = fixedAssetService.findById(assetId);
        List<DecliningBalanceDepreciation> results = new ArrayList<>();
        BigDecimal remainingValue = asset.getInitialCost();
        BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

        for (int year = 1; year <= asset.getUsefulLife(); year++) {
            BigDecimal annualDepreciation = remainingValue.multiply(depreciationRate);

            if (remainingValue.subtract(annualDepreciation).compareTo(asset.getResidualValue()) < 0) {
                annualDepreciation = remainingValue.subtract(asset.getResidualValue());
            }

            DecliningBalanceDepreciation calculation = new DecliningBalanceDepreciation();
            calculation.setFixedAsset(asset);
            calculation.setCalculationDate(getCalculationDate(asset.getPurchaseDate(), year));
            calculation.setDepreciationAmount(annualDepreciation);
            accumulatedDepreciation = accumulatedDepreciation.add(annualDepreciation);
            calculation.setAccumulatedDepreciation(accumulatedDepreciation);
            calculation.setDepreciationMethod("Метод остаточной стоимости");
            calculation.setInitialCost(asset.getInitialCost());
            calculation.setResidualValue(asset.getInitialCost().subtract(accumulatedDepreciation));
            calculation.setDepreciationRate(depreciationRate);

            results.add(calculation);
            remainingValue = calculation.getResidualValue();
        }
        return results;
    }


    private String getCalculationDate(String purchaseDate, int year) {
        LocalDate purchaseLocalDate = LocalDate.parse(purchaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return purchaseLocalDate.plusYears(year).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    private void handleGetAllAmortization() {
        DepreciationCalculationService depreciationCalculationService = new DepreciationCalculationService();

        List<DepreciationCalculation> amortizations = depreciationCalculationService.findAll();

        if (amortizations != null && !amortizations.isEmpty()) {
            response.setMessage(gson.toJson(amortizations));
            response.setSuccess(true);
        } else {
            response.setMessage("Не найдено амортизации.");
            response.setSuccess(false);
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }



    private Comparator<FixedAsset> getComparator(String sortBy, String sortOrder) {
        Comparator<FixedAsset> comparator = null;
        switch (sortBy) {
            case "name":
                comparator = Comparator.comparing(FixedAsset::getName);
                break;
            case "inventoryNumber":
                comparator = Comparator.comparing(FixedAsset::getInventoryNumber);
                break;
            case "purchaseDate":
                comparator = Comparator.comparing(FixedAsset::getPurchaseDate);
                break;
            case "initialCost":
                comparator = Comparator.comparing(FixedAsset::getInitialCost);
                break;
            case "usefulLife":
                comparator = Comparator.comparing(FixedAsset::getUsefulLife);
                break;
            case "residualLife":
                comparator = Comparator.comparing(FixedAsset::getResidualValue);
                break;
            case "depreciationMethod":
                comparator = Comparator.comparing(FixedAsset::getDepreciationMethod);
                break;
            case "category":
                comparator = Comparator.comparing(FixedAsset::getCategory);
                break;
            default:
                return null; // Неизвестный параметр сортировки
        }

        // Определяем порядок сортировки
        if ("DESC".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private void handleDeleteUser(Request request) {
        int userId = Integer.parseInt(request.getMessage());
        userService.delete(userId);
        response.setMessage("Пользователь успешно удален.");
        response.setSuccess(true);
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleGetUser(Request request) {
        int userId = Integer.parseInt(request.getMessage());
        UserService userService = new UserService();
        User user = userService.findById(userId);
        if (user != null) {
            response.setMessage(gson.toJson(user));
            response.setSuccess(true);
        } else {
            response.setMessage("Пользователь не найден.");
            response.setSuccess(false);
        }
        writer.println(gson.toJson(response));
        writer.flush();
    }

    public void handleGetAllUsers() {
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(Role.class, new RoleSerializer())
                .create();

       /* GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY); // Handle Hibernate proxies
        Gson gson = builder.create();*/

        UserService userService = new UserService();
        List<User> users = userService.findAll();

        if (users != null && !users.isEmpty()) {
            response.setMessage(gson1.toJson(users));
            response.setSuccess(true);
        } else {
            response.setMessage("Не найдено пользователей.");
            response.setSuccess(false);
        }

        writer.println(gson1.toJson(response));
        writer.flush();
    }

    private void handleUpdateUser(Request request) {
        User receivedUser = gson.fromJson(request.getMessage(), User.class);
        UserService userService = new UserService();

        // Ищем пользователя в БД по ID
        User existingUser = userService.findById(receivedUser.getId());
        if (existingUser != null) {
            // Обновляем только измененные поля
            existingUser.setLogin(receivedUser.getLogin());
            existingUser.setEmail(receivedUser.getEmail());
            existingUser.setPassword(receivedUser.getPassword());

            // Не обновляем роль, так как она не была передана
            userService.update(existingUser);

            response.setMessage("Пользователь успешно обновлен.");
            response.setSuccess(true);
        } else {
            response.setMessage("Пользователь не найден.");
            response.setSuccess(false);
        }

        writer.println(gson.toJson(response));
        writer.flush();
    }


    private void handleSendMessage(Request request) {
        Response response = new Response();
        try {
            JsonObject requestParams = gson.fromJson(request.getMessage(), JsonObject.class);

            JsonObject receiverJson = requestParams.getAsJsonObject("receiver");
            User receiver = gson.fromJson(receiverJson, User.class);

            // Получаем sender (аналогично receiver)
            JsonObject senderJson = requestParams.getAsJsonObject("sender");
            User sender = gson.fromJson(senderJson, User.class);

            // Получаем роль отправителя (если это вложенный объект, то извлекаем его)
            JsonObject roleJson = requestParams.getAsJsonObject("role");
            String senderRole = null;
            if (roleJson != null) {
                senderRole = roleJson.get("name").getAsString();  // Предполагаем, что в объекте role есть поле "name"
            }

            // Получаем другие поля
            boolean isRead = requestParams.get("isRead").getAsBoolean(); // Преобразуем в boolean
            String content = requestParams.get("content").getAsString();

            // Создаем объект сообщения
            Message message = new Message(sender, receiver, content, isRead);  // Включаем sender

            // Вставка сообщения в базу данных
            MessageService messageService = new MessageService();
            messageService.insert(message);  // Вставка сообщения в базу данных

            response.setMessage("Сообщение успешно отправлено");
            response.setSuccess(true);
        } catch (Exception e) {
            // Обработка ошибок
            response.setSuccess(false);
            response.setMessage("Ошибка при сохранении сообщения: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Отправляем ответ клиенту
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    private void handleGetMessages(Request request) {
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(Role.class, new RoleSerializer())  // Если у вас есть кастомный сериализатор для роли
                .create();

        User currentUser = gson1.fromJson(request.getMessage(), User.class);

        MessageService messageService = new MessageService();
        List<Message> allMessages = messageService.findMessagesByUser(currentUser);

        Response response = new Response();
        if (allMessages != null && !allMessages.isEmpty()) {
            response.setSuccess(true);
            response.setMessage(gson1.toJson(allMessages)); // сериализуем список сообщений
        } else {
            response.setSuccess(false);
            response.setMessage("Сообщения не найдены.");
        }
        System.out.println("Отправка ответа клиенту: " + gson1.toJson(response));
        writer.println(gson1.toJson(response)); // отправляем ответ
        writer.flush();
    }


    private void handleMarkAsRead(Request request) {
        Response response = new Response();
        try {
            JsonObject requestParams = gson.fromJson(request.getMessage(), JsonObject.class);
            int messageId = requestParams.get("messageId").getAsInt();

            MessageService messageService = new MessageService();
            boolean updateSuccess = messageService.markMessageAsRead(messageId);

            if (updateSuccess) {
                response.setSuccess(true);
                response.setMessage("Сообщение успешно отмечено как прочитанное.");
            } else {
                response.setSuccess(false);
                response.setMessage("Не удалось отметить сообщение. Проверьте ID сообщения.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Ошибка при обработке запроса: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }





    private void sendSuccessResponse(String message) {
        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);
        writer.println(gson.toJson(response));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
