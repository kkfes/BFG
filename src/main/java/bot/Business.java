package bot;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Business {

    private static final ArrayList<Business> businesses = new ArrayList<>();
    static {
        new Business(1,0,"Шаурмечная",50000,500,"\uD83C\uDF2F").addStatic();
        new Business(2,0,"Ночной клуб",500000,5000,"\uD83D\uDD7A").addStatic();
        new Business(3,0,"Кальянная",1000000,8000,"\uD83D\uDEAC").addStatic();
        new Business(4,0,"АЗС",1500000,12000,"⛽️").addStatic();
        new Business(5,0,"Порностудия",3000000,25000,"\uD83C\uDFE9").addStatic();
        new Business(6,0,"Маленький офис",7000000,25000,"\uD83C\uDFE2").addStatic();
        new Business(7,0,"Нефтевышка",10000000,60000,"\uD83D\uDEE2").addStatic();
        new Business(8,0,"Космическое агентство",20000000,150000,"\uD83D\uDC69").addStatic();
        new Business(9,0,"Межпланетный экспресс",40000000,250000,"\uD83D\uDE80").addStatic();
    }
    private void addStatic(){
        businesses.add(this);
    }


    private final String icon;
    private final int num;
    private long id;
    private final String name;
    private int workers;
    private final int cost;
    private final int profit;
    private long unix;

    public Business(int num, long id, String name, int cost, int profit,String icon) {
        this.num = num;
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.profit = profit;
        this.icon=icon;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public int getCost() {
        return cost;
    }

    public int getProfit() {
        return profit;
    }

    public long getUnix() {
        return unix;
    }

    public void setUnix(long unix) {
        this.unix = unix;
    }

    public int getNum() {
        return num;
    }

    public void addBusiness(long unix){
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("INSERT INTO `business`(`id`, `name`, `workers`, `cost`, `profit`, `unix`) VALUES ('"+this.id+"','"+this.name+"','0','"+this.cost+"','"+this.profit+"','"+unix+"')");
            statement.executeUpdate();
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void save(){
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("UPDATE `business` SET `name`='"+this.name+"',`workers`='"+this.workers+"',`cost`='"+this.cost+"',`profit`='"+this.profit+"',`unix`='"+this.unix+"' WHERE `id`='"+this.id+"'");
            statement.executeUpdate();
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void delete(){
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("DELETE FROM business WHERE id = ?");
            statement.setLong(1,this.id);
            statement.executeUpdate();
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Business get(long id){
        Business business = null;
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("SELECT * FROM business WHERE id = ?");
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                business= new Business(0,resultSet.getLong("id"),resultSet.getString("name"),resultSet.getInt("cost"),resultSet.getInt("profit"),"0");
                business.setWorkers(resultSet.getInt("workers"));
                business.setUnix(resultSet.getLong("unix"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return business;
    }


    private static String buyBusiness(long id,String text,long unix){
        Users users = Users.getUser(id);
        int num = 0;
        try {
            num = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        Business business = null;
        for(Business b:businesses){
            if(b.num==num){
                business=b;
            }
        }
        if(business!=null){
            if(get(id)==null){
                if(users.getMoney()>= business.cost){
                    users.setMoney(users.getMoney()- business.cost);
                    users.save();
                    business.id=id;
                    business.setUnix(unix);
                    business.setWorkers(0);
                    business.addBusiness(unix);
                    return Users.setLink(id)+", вы успешно купили бизнес \""+ business.name+"\" \uD83D\uDE04";
                }else {
                    return Users.setLink(id)+", у вас недостаточно денег \uD83D\uDE1F";
                }
            }else {
                return Users.setLink(id)+", у вас уже есть бизнес, вы не можете иметь два бизнеса \uD83D\uDE22";
            }
        }else {
            return "Данного бизнеса не существует. Проверьте введенный вами ID.";
        }
    }


    private static String myBusiness(long id,long unix){
        Business business = get(id);
        if(business!=null){
            int a = business.profit*business.workers/3600;
            int sec = (int) (unix-business.unix);
            if(sec>68400){
                sec=86400;
            }
            long pr = (long) sec *a;
            return Users.setLink(id)+", ваш бизнес:\n" +
                    "<b>ℹ️ Название бизнеса:</b> "+business.name+"\n" +
                    "<b>\uD83D\uDCB8 Прибыль:</b> <code>"+business.profit+"$/чаc</code>\n" +
                    "<b>\uD83D\uDCBC Рабочих:</b> <code>"+business.workers+"/450</code>\n" +
                    "<b>\uD83D\uDCB0 На счету:</b> <code>"+pr+"</code>$\n" +
                    "<b>\uD83E\uDD11 Бонус за рабочих:</b> <code>"+business.profit*business.workers+"</code>$";
        }else {
            return Users.setLink(id)+", к сожалению у вас нет бизнеса \uD83D\uDE15\n" +
                    "Для просмотра достуных бизнесов введите - \"Бизнесы\"";
        }
    }

    private static String allBusinesses(){
        StringBuilder text = new StringBuilder("Доступные бизнесы для покупки:\n");
        for (Business b:businesses){
            text.append(b.icon).append(" ").append(b.num).append(". ").append(b.name).append(": ").append(b.cost).append("$\n").append("Прибыль: ").append(b.profit).append("$/час\n\n");
        }
        text.append("\uD83D\uDCA1 Вы можете купить только ОДИН обычный бизнес.\n" + "\uD83D\uDED2 Для покупки введите \"Купить бизнес [номер]\"");
        return text.toString();
    }

    private static String buyWorkers(long id,String text){
        Users users = Users.getUser(id);
        int num = 0;
        try {
            num = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        Business business = get(id);
        if(business!=null){
            if(num!=0){
                if(business.workers+num<=450){
                    int cost = business.cost*num;
                    if(users.getMoney()>=cost){
                        users.setMoney(users.getMoney()-cost);
                        users.save();
                        business.setWorkers(business.workers+num);
                        business.save();
                        return Users.setLink(id)+", вы успешно купили "+num+" рабочих за "+cost+"$ \uD83D\uDE03";
                    }else {
                        return Users.setLink(id)+", у вас недостаточно денег для покупки рабочих для вашего бизнеса \uD83D\uDE22";
                    }
                }else {
                    return Users.setLink(id)+", вы не можете купить столько рабочих \uD83D\uDE1E";
                }
            }else {
                return Users.setLink(id)+", вы ввели не число для покупки рабочих в бизнес \uD83D\uDE22";
            }
        }else {
            return Users.setLink(id)+", к сожалению у вас нет бизнеса \uD83D\uDE1F";
        }
    }

    private static String sellBusiness(long id){
        Users users = Users.getUser(id);
        Business business = get(id);
        if(business!=null){
            business.delete();
            users.setMoney(users.getMoney()+business.cost);
            users.save();
            return Users.setLink(id)+", вы успешно продали бизнес \""+business.name+"\" \uD83D\uDE0A";
        }else {
            return Users.setLink(id)+", у вас нет бизнеса \uD83D\uDE1E";
        }
    }

    private static String getProfit(long id,String text,long unix){
        Users users = Users.getUser(id);
        long num = 0;
        try {
            num = Long.parseLong(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        Business business = get(id);
        if(num!=0){
            if(business!=null){
                long a = (long) business.profit *business.workers/3600;
                long sec = (int) (unix-business.unix);
                if(sec>68400){
                    sec=86400;
                }
                long pr = sec *a;
                business.unix=unix;
                business.save();
                users.setMoney((int) (users.getMoney()+pr));
                users.save();
                return Users.setLink(id)+", вы успешно сняли "+pr+"$ с вашего бизнеса \uD83D\uDE04";
            }else {
                return Users.setLink(id)+", к сожалению у вас нет бизнеса \uD83D\uDE15";
            }
        }else {
            return Users.setLink(id)+", вы не ввели количество денег которое хотите снять \uD83D\uDE15";
        }
    }

    private static String businessHelp(){
        return """
                \uD83D\uDDC4 Бизнес:
                   \uD83D\uDCB0 Мой бизнес
                   \uD83D\uDCB5 Купить бизнес [номер]
                   \uD83D\uDC77 Купить рабочих [кол-во]
                   \uD83D\uDCB8 Продать бизнес
                   \uD83D\uDCB2 Бизнес снять [кол-во]
                   \uD83C\uDD98 !Бизнес""";
    }

    public static void businessCommands(Update update){
        String message = update.getMessage().getText();
        long user_id = update.getMessage().getFrom().getId();
        long chat_id = update.getMessage().getChatId();
        long unix = update.getMessage().getDate();
        if(message.toLowerCase().startsWith("купить бизнес")){
            BFGBot.sendMessage(chat_id,buyBusiness(user_id,message,unix),false);
        }else if(message.equalsIgnoreCase("Мой бизнес")){
            BFGBot.sendMessage(chat_id,myBusiness(user_id,unix),false);
        }else if(message.equalsIgnoreCase("бизнесы")){
            BFGBot.sendMessage(chat_id,allBusinesses(),false);
        }else if(message.toLowerCase().startsWith("купить рабочих")){
            BFGBot.sendMessage(chat_id,buyWorkers(user_id,message),false);
        }else if(message.equalsIgnoreCase("продать бизнес")){
            BFGBot.sendMessage(chat_id,sellBusiness(user_id),false);
        }else if(message.toLowerCase().startsWith("бизнес снять")){
            BFGBot.sendMessage(chat_id,getProfit(user_id,message,unix),false);
        }else if(message.equalsIgnoreCase("!бизнес")){
            BFGBot.sendMessage(chat_id,businessHelp(),false);
        }
    }
}
