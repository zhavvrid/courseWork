package com.example.server.Utility;

import com.example.server.Enums.RequestType;
import com.example.server.Models.Entities.DepreciationCalculation;
import com.example.server.Models.Entities.FixedAsset;
import com.example.server.Models.Entities.Role;
import com.example.server.Models.Entities.User;
import com.example.server.Models.TCP.HibernateProxyTypeAdapter;
import com.example.server.Models.TCP.Request;
import com.example.server.Models.TCP.Response;
import com.example.server.Models.TCP.RoleSerializer;
import com.example.server.Services.DepreciationCalculationService;
import com.example.server.Services.FixedAssetService;
import com.example.server.Services.RoleService;
import com.example.server.Services.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.hibernate.Hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.math.MathContext;


public class ClientThread implements Runnable {
    private Socket clientsSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Gson gson;
    private Response response;
    private Request request;


    private UserService userService = new UserService();
    private RoleService roleService = new RoleService();
    private FixedAssetService fixedAssetService = new FixedAssetService();

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
        try {
            User user = gson.fromJson(request.getMessage(), User.class);
            System.out.println("Попытка входа для пользователя: " + user.getLogin());

            User foundUser = userService.login(user.getLogin(), user.getPassword());

            if (foundUser != null) {
                response.setMessage("Успешный вход для пользователя: " + foundUser.getLogin());
                response.setSuccess(true);
                response.setResponseType(RequestType.LOGIN);
                response.setRole(foundUser.getRole().getName()); // Add this line to include the role
            } else {
                response.setMessage("Ошибка: Неверный логин или пароль.");
                response.setSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
        }
        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }

    private void handleAddAsset(Request request) {
        FixedAsset asset = gson.fromJson(request.getMessage(), FixedAsset.class);

        System.out.println("До сохранения: id = " + asset.getId());

        // Сохраняем актив через сервис
        fixedAssetService.insert(asset);

        // Проверим id после сохранения
        System.out.println("После сохранения: id = " + asset.getId());

        // Устанавливаем успешный ответ
        response.setId(asset.getId());
        response.setMessage("Актив успешно добавлен.");
        response.setSuccess(true);

        // Отправляем ответ клиенту
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

    private void handleCalculateAmortizationRequest(Request request) {
        Response response = new Response();
        DepreciationCalculationService calculationService = new DepreciationCalculationService();

        try {
            JsonObject requestParams = gson.fromJson(request.getMessage(), JsonObject.class);
            int assetId = requestParams.get("assetId").getAsInt();
            String method = requestParams.get("method").getAsString();


            List<DepreciationCalculation> amortizationResults;
            switch (method) {
                case "Линейный":
                    amortizationResults = calculateStraightLineDepreciation(assetId);
                    break;
                case "Ускоренный":
                    amortizationResults = calculateAcceleratedDepreciation(assetId);
                    break;
                case "Метод остаточной стоимости":
                    amortizationResults = calculateDecliningBalanceDepreciation(assetId);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный метод амортизации: " + method);
            }

            // Сохраняем результаты в базе данных
            for (DepreciationCalculation calculation : amortizationResults) {
                calculationService.insert(calculation);
            }

            response.setSuccess(true);
            response.setMessage(gson.toJson(amortizationResults));  // Возвращаем JSON с результатами
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Ошибка при расчете амортизации: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    private List<DepreciationCalculation> calculateStraightLineDepreciation(int assetId) {
        FixedAsset asset = fixedAssetService.findById(assetId);
        List<DepreciationCalculation> results = new ArrayList<>();

        BigDecimal annualDepreciation = (asset.getInitialCost().subtract(asset.getResidualValue()))
                .divide(BigDecimal.valueOf(asset.getUsefulLife()), BigDecimal.ROUND_HALF_UP);

        LocalDate purchaseDate = LocalDate.parse(asset.getPurchaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (int year = 1; year <= asset.getUsefulLife(); year++) {
            DepreciationCalculation calculation = new DepreciationCalculation();
            calculation.setFixedAsset(asset);

            LocalDate calculationDate = purchaseDate.plusYears(year);
            calculation.setCalculationDate(calculationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            calculation.setDepreciationAmount(annualDepreciation);
            calculation.setAccumulatedDepreciation(annualDepreciation.multiply(BigDecimal.valueOf(year)));
            calculation.setDepreciationMethod("Линейный");
            calculation.setInitialCost(asset.getInitialCost());
            calculation.setResidualValue(asset.getResidualValue());

            BigDecimal currentResidualValue = asset.getInitialCost().subtract(calculation.getAccumulatedDepreciation());
            calculation.setResidualValue(currentResidualValue);

            results.add(calculation);
        }

        return results;
    }

    private List<DepreciationCalculation> calculateAcceleratedDepreciation(int assetId) {
        FixedAsset asset = fixedAssetService.findById(assetId);
        List<DepreciationCalculation> results = new ArrayList<>();

        BigDecimal usefulLife = BigDecimal.valueOf(asset.getUsefulLife());
        if (usefulLife.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Ошибка: Срок службы актива не может быть нулем или отрицательным.");
            return results;
        }

        BigDecimal depreciationRate = BigDecimal.valueOf(2).divide(usefulLife, new MathContext(10));
        System.out.println("Коэффициент амортизации: " + depreciationRate);
        BigDecimal remainingValue = asset.getInitialCost();
        BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

        System.out.println("Начальная стоимость актива: " + asset.getInitialCost());
        System.out.println("Остаточная стоимость: " + asset.getResidualValue());
        System.out.println("Срок службы: " + asset.getUsefulLife());

        for (int year = 1; year <= asset.getUsefulLife(); year++) {
            BigDecimal annualDepreciation = remainingValue.multiply(depreciationRate).setScale(2, BigDecimal.ROUND_DOWN);
            System.out.println("Год " + year + ": Начальная оставшаяся стоимость " + remainingValue + ", Амортизация " + annualDepreciation);

            BigDecimal potentialResidualValue = remainingValue.subtract(annualDepreciation);
            if (potentialResidualValue.compareTo(asset.getResidualValue()) < 0) {
                annualDepreciation = remainingValue.subtract(asset.getResidualValue());
                System.out.println("Скорректированная амортизация (не может быть ниже остаточной стоимости): " + annualDepreciation);
            }

            DepreciationCalculation calculation = new DepreciationCalculation();
            calculation.setFixedAsset(asset);
            calculation.setCalculationDate(getCalculationDate(asset.getPurchaseDate(), year));
            calculation.setDepreciationAmount(annualDepreciation);

            accumulatedDepreciation = accumulatedDepreciation.add(annualDepreciation);
            calculation.setAccumulatedDepreciation(accumulatedDepreciation);

            calculation.setDepreciationMethod("Ускоренный");
            calculation.setInitialCost(asset.getInitialCost());

            BigDecimal currentResidualValue = asset.getInitialCost().subtract(accumulatedDepreciation);
            calculation.setResidualValue(currentResidualValue);

            results.add(calculation);
            remainingValue = currentResidualValue;

            System.out.println("Год " + year + ": Накопленная амортизация " + accumulatedDepreciation + ", Остаточная стоимость " + currentResidualValue);
        }

        return results;
    }

    private List<DepreciationCalculation> calculateDecliningBalanceDepreciation(int assetId) {
        FixedAsset asset = fixedAssetService.findById(assetId);
        List<DepreciationCalculation> results = new ArrayList<>();

        BigDecimal depreciationRate = BigDecimal.valueOf(0.2);
        BigDecimal remainingValue = asset.getInitialCost();
        BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

        for (int year = 1; year <= asset.getUsefulLife(); year++) {
            // Рассчитываем сумму амортизации для текущего года
            BigDecimal annualDepreciation = remainingValue.multiply(depreciationRate);

            // Проверяем, чтобы остаточная стоимость не была ниже минимально допустимой
            if (remainingValue.subtract(annualDepreciation).compareTo(asset.getResidualValue()) < 0) {
                annualDepreciation = remainingValue.subtract(asset.getResidualValue());
            }

            // Создаем новый объект для хранения расчетов амортизации
            DepreciationCalculation calculation = new DepreciationCalculation();
            calculation.setFixedAsset(asset);

            // Устанавливаем дату расчета
            calculation.setCalculationDate(getCalculationDate(asset.getPurchaseDate(), year));

            // Устанавливаем сумму амортизации
            calculation.setDepreciationAmount(annualDepreciation);

            // Обновляем накопленную амортизацию
            accumulatedDepreciation = accumulatedDepreciation.add(annualDepreciation);
            calculation.setAccumulatedDepreciation(accumulatedDepreciation);

            // Устанавливаем метод амортизации
            calculation.setDepreciationMethod("Метод остаточной стоимости");

            // Устанавливаем начальную стоимость актива
            calculation.setInitialCost(asset.getInitialCost());

            // Рассчитываем остаточную стоимость
            BigDecimal currentResidualValue = asset.getInitialCost().subtract(accumulatedDepreciation);
            calculation.setResidualValue(currentResidualValue);

            // Добавляем расчет в список результатов
            results.add(calculation);

            // Обновляем оставшуюся стоимость для следующего года
            remainingValue = currentResidualValue;
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
        User user = gson.fromJson(request.getMessage(), User.class);
        UserService userService = new UserService();
        userService.update(user);
        response.setMessage("Пользователь успешно обновлен.");
        response.setSuccess(true);
        writer.println(gson.toJson(response));
        writer.flush();
    }
    private void sendSuccessResponse(String message) {
        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);
        writer.println(gson.toJson(response));
    }

}
