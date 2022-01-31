package bot;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Users {

    private static long bitcoinCourse = 40000 + (int) (Math.random() * 50000);

    private final long id;
    private String user_name;
    private String user_fullname;
    private int money = 0;
    private int bitcoin = 0;
    private int bank = 0;
    private final long registration;
    private int experience = 0;
    private int business = 0;
    private long bonus = 0;
    private long hall = 0;


    public Users(long id, String user_name, String user_fullname, long registration,long bonus) {
        this.id = id;
        this.user_name = user_name;
        this.user_fullname = user_fullname;
        this.registration = registration;
        this.bonus=bonus;
    }

    public long getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getBitcoin() {
        return bitcoin;
    }

    public void setBitcoin(int bitcoin) {
        this.bitcoin = bitcoin;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public long getRegistration() {
        return registration;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public long getBonus() {
        return bonus;
    }

    public void setBonus(long bonus) {
        this.bonus = bonus;
    }

    public long getHall() {
        return hall;
    }

    public void setHall(long hall) {
        this.hall = hall;
    }

    public void add(){
        if(getUser(this.id)==null){
            try {
                PreparedStatement statement = DBManager.c.prepareStatement("INSERT INTO `users`(`id`, `user_fullname`, `user_name`, `money`, `bitcoin`, `bank`, `registration`, `experience`, `business`, `bonus`, `hall`) VALUES ('"+this.id+"','"+this.user_fullname+"','"+this.user_name+"','"+this.money+"','"+this.bitcoin+"','"+this.bank+"','"+this.registration+"','"+this.experience+"','"+this.business+"','"+0+"','0')");
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(){
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("UPDATE `users` SET `user_fullname`='"+this.user_fullname+"',`user_name`='"+this.user_name+"',`money`='"+this.money+"',`bitcoin`='"+this.bitcoin+"',`bank`='"+this.bank+"',`registration`='"+this.registration+"',`experience`='"+this.experience+"',`business`='"+this.business+"',`bonus`='"+this.bonus+"',`hall`='"+this.hall+"' WHERE `id`='"+this.id+"'");
            statement.executeUpdate();
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Users getUser(long id){
        Users users = null;
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("SELECT * FROM `users` WHERE `id`='"+id+"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                users = new Users(resultSet.getLong("id"),resultSet.getString("user_name"),resultSet.getString("user_fullname"),resultSet.getLong("registration"),resultSet.getLong("bonus"));
                users.setMoney(resultSet.getInt("money"));
                users.setBitcoin(resultSet.getInt("bitcoin"));
                users.setBank(resultSet.getInt("bank"));
                users.setExperience(resultSet.getInt("experience"));
                users.setBusiness(resultSet.getInt("business"));
                users.setHall(resultSet.getLong("hall"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }

    public static String setLink(long id){
        return "<a href='tg://openmessage?user_id="+id+"'>"+getUser(id).user_fullname+"</a>";
    }

    private static String getUserProfile(long id){
        Users users = getUser(id);
        String im = "";
        Business business = Business.get(id);
        if(business!=null){
            im="\uD83D\uDCBC Бизнес: "+ business.getName()+"\n";
        }
        return ""+setLink(id)+", ваш профиль:\n" +
                "<b>\uD83D\uDD0E ID:</b> <code>"+id+"</code>\n" +
                "<b>\uD83D\uDCB0 Денег:</b> <code>"+users.money+"$</code>\n" +
                "<b>\uD83C\uDFE6 В банке:</b> <code>"+users.bank+"$</code>\n" +
                "<b>\uD83D\uDCBD Биткоины:</b> <code>"+users.bitcoin+"฿</code>\n" +
                "<b>\uD83C\uDF1F Опыт:</b> <code>"+users.experience+"</code>\n" +
                "<b>\uD83D\uDC51 Рейтинг:</b> <code>"+users.business+"</code>\n" +
                "\n" +
                "\uD83D\uDCE6 Имущество:\n" +
                im+"\n" +
                "<b>\uD83D\uDCC5 Дата регистрации:</b>\n<code>" +
                new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date (users.registration*1000))+"</code>";
    }

    private static String balance(long id){
        Users users = getUser(id);
        return "<b>\uD83D\uDC6BНик: "+setLink(id)+"</b>\n" +
                "<b>\uD83D\uDCB0Деньги:</b> <code>"+users.money+"$</code>\n" +
                "<b>\uD83C\uDFE6Банк:</b> <code>"+users.bank+"$</code>\n" +
                "<b>\uD83D\uDCBDБиткоины:</b> <code>"+users.bitcoin+"\uD83C\uDF10</code>";
    }



    private static String dailyBonus(long id,long unix){
        Users users = getUser(id);
        if(unix-users.bonus>=86400){
            users.setBonus(unix);
            Random rand = new Random();
            int i = rand.nextInt(5);
            if(i==0){
                int how;
                if(users.money>=10){
                    how = (users.money / 2) + (int) (Math.random() * (users.money * 2));
                }else {
                    how = 500 + (int) (Math.random() * 1000000);
                }
                users.setMoney(users.money+how);
                users.save();
                return setLink(id)+", вам был выдан ежедневный бонус в размере "+how+"$ \uD83D\uDCB0";
            }else if(i==1){
                int how;
                if(users.experience>=10){
                    how = (users.experience / 5) + (int) (Math.random() * (users.experience * 2));
                }else {
                    how = 10 + (int) (Math.random() * 500);
                }
                users.setExperience(users.experience+how);
                users.save();
                return setLink(id)+", вам был выдан ежедневный бонус в размере "+how+" опыта \uD83C\uDFC6";
            }else if(i==2){
                int how = rand.nextInt(1000);
                users.setBusiness(users.business+how);
                users.save();
                return setLink(id)+", вам был выдан ежедневный бонус в размере "+how+" рейтинга \uD83D\uDC51";
            }else {
                int how;
                if(users.money>=10){
                    how = (users.money / 2) + (int) (Math.random() * (users.money * 2));
                }else {
                    how = 500 + (int) (Math.random() * 1000000);
                }
                users.setBank(users.bank+how);
                users.save();
                return setLink(id)+", вам был выдан ежедневный бонус в размере "+how+"$ \uD83D\uDCB0 в банк";
            }
        }else {
            return ""+setLink(id)+", ты уже получал(-а) ежедневный бонус, следующий бонус ты сможешь получить через "+Time(86400-(unix-users.bonus),true);
        }
    }

    private static String rabHall(long id,long unix){
        Users users = getUser(id);
        if(unix-users.hall>=86400){
            users.setHall(unix);
            if(new Random().nextBoolean()){
                int how = new Random().nextInt(1000000);
                users.setMoney(users.money+how);
                users.save();
                return setLink(id)+", вы успешно ограбили казну. На ваш баланс зачислено "+how+" ✅";
            }else {
                users.save();
                return setLink(id)+", к сожалению вам не удалось ограбить казну ❎";
            }
        }else {
            return setLink(id)+", вы уже грабили казну сегодня. Бегите скорее, полиция уже в пути \uD83D\uDEAB";
        }
    }

    private static String bitcoinCourse(long id){
        return setLink(id)+", на данный момент курс 1 BTC составляет - "+bitcoinCourse+"$ \uD83C\uDF10";
    }

    private static String buyBitcoin(long id,String text){
        Users users = getUser(id);
        int how = 0;
        try {
            how=Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        }catch (Exception ignored){
        }
        if(how!=0){
            int cost = (int) (bitcoinCourse*how);
            if(users.money>=cost){
                users.setMoney(users.money-cost);
                users.setBitcoin(users.bitcoin+how);
                users.save();
                if(new Random().nextBoolean()){
                    bitcoinCourse+=new Random().nextInt(100);
                }else {
                    bitcoinCourse-=new Random().nextInt(100);
                }
                return setLink(id)+", вы успешно купили "+how+" BTC за "+cost+"$ \uD83D\uDE42";
            }else {
                return setLink(id)+", у вас недостаточно денег для покупки BTC \uD83D\uDE15";
            }
        }else {
            return setLink(id)+", вы не ввели количество BTC которое хотите купить \uD83D\uDE1E";
        }
    }

    public static String sellBitcoin(long id,String text) {
        Users users = getUser(id);
        int how = users.bitcoin;
        try {
            how = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        int cost = (int) (how*bitcoinCourse);
        if (users.bitcoin >= how) {
            users.setMoney(users.money + cost);
            users.setBitcoin(users.bitcoin - how);
            users.save();
            if (new Random().nextBoolean()) {
                bitcoinCourse += new Random().nextInt(100);
            } else {
                bitcoinCourse -= new Random().nextInt(100);
            }
            return setLink(id) + ", вы успешно продали "+how+" BTC за "+cost+"$ \uD83D\uDE03";
        } else {
            return setLink(id) + ", вы не можете продать столько BTC \uD83D\uDE15";
        }
    }

    private static String bitcoinBalance(long id){
        return setLink(id)+", на вашем балансе "+getUser(id).bitcoin+" BTC \uD83C\uDF10";
    }

    private static String topUsers(long id){
        StringBuilder text = new StringBuilder(setLink(id) + ", топ 10 игроков бота:\n");
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("SELECT * FROM users ORDER BY business DESC LIMIT 10");
            ResultSet resultSet = statement.executeQuery();
            int i = 1;
            while (resultSet.next()){
                text.append(i).append("⃣ ♣️").append(resultSet.getString("user_fullname")).append("️ — \uD83D\uDC51").append(resultSet.getInt("business")).append(" | $").append(resultSet.getInt("money")).append("\n");
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Users users = getUser(id);
        text.append("—————————————————\n").append(setLink(id)).append(" — \uD83D\uDC51").append(users.business).append(" | $").append(users.money);
        return text.toString();
    }

    private static String bankPut(long id,String text){
        Users users = getUser(id);
        int how = 0;
        try {
            how = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        if(how!=0){
            if(users.money>=how){
                users.setMoney(users.money-how);
                users.setBank(users.bank+how);
                users.save();
                return setLink(id)+", вы успешно положили на банковский счёт "+how+"$ \uD83D\uDE0A";
            }else {
                return setLink(id)+", вы не можете положить в банк больше чем у вас на балансе \uD83D\uDE22";
            }
        }else {
            return null;
        }
    }

    private static String bankGet(long id,String text){
        Users users = getUser(id);
        int how = 0;
        try {
            how = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        if(how!=0){
            if(users.bank>=how){
                users.setMoney(users.money+how);
                users.setBank(users.bank-how);
                users.save();
                return setLink(id)+", вы успешно сняли с банковского счёта "+how+"$ \uD83D\uDE0A";
            }else {
                return setLink(id)+", вы не можете снять с банка больше чем у вас есть \uD83D\uDE1E";
            }
        }else {
            return null;
        }
    }

    private static String rating(long id){
        return setLink(id)+", ваш рейтинг "+getUser(id).business+"\uD83D\uDC51";
    }

    private static String sellRating(long id,String text){
        Users users = getUser(id);
        int how = 0;
        try {
            how = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        if(how!=0){
            if(users.business>=how){
                users.setBusiness(users.business-how);
                users.setMoney(users.money+(how*100000000));
                users.save();
                return setLink(id)+", вы понизили количество вашего рейтинга на "+how+"\uD83D\uDC51 за "+how*100000000+"$ \uD83D\uDE04";
            }else {
                return setLink(id)+", вы не можете снять с банка больше чем у вас есть \uD83D\uDE1E";
            }
        }else {
            return setLink(id)+", вы неправильно ввели число рейтинга которое хотите продать \uD83D\uDE1E";
        }
    }

    public static void userCommands(Update update){
        String message = update.getMessage().getText();
        long user_id = update.getMessage().getFrom().getId();
        long chat_id = update.getMessage().getChatId();
        long unix = update.getMessage().getDate();
        if(message.equalsIgnoreCase("Профиль")){
            BFGBot.sendMessage(chat_id,getUserProfile(user_id),false);
        }else if(message.equalsIgnoreCase("ежедневный бонус")){
            BFGBot.sendMessage(chat_id,dailyBonus(user_id,unix),false);
        }else if(message.equalsIgnoreCase("б")||message.equalsIgnoreCase("баланс")){
            BFGBot.sendMessage(chat_id,balance(user_id),false);
        }else if(message.equalsIgnoreCase("Ограбить мэрию")){
            BFGBot.sendMessage(chat_id,rabHall(user_id,unix),false);
        }else if(message.equalsIgnoreCase("казна")){
            BFGBot.sendMessage(chat_id,getTreasury(),false);
        }else if(message.equalsIgnoreCase("Биткоин курс")){
            BFGBot.sendMessage(chat_id,bitcoinCourse(user_id),false);
        }else if(message.toLowerCase().startsWith("биткоин купить")){
            BFGBot.sendMessage(chat_id,buyBitcoin(user_id,message),false);
        }else if(message.toLowerCase().startsWith("биткоин продать")){
            BFGBot.sendMessage(chat_id,sellBitcoin(user_id,message),false);
        }else if(message.equalsIgnoreCase("Биткоины")){
            BFGBot.sendMessage(chat_id,bitcoinBalance(user_id),false);
        }else if(message.equalsIgnoreCase("топ")){
            BFGBot.sendMessage(chat_id,topUsers(user_id),false);
        }else if(message.toLowerCase().startsWith("банк положить")){
            BFGBot.sendMessage(chat_id,bankPut(user_id,message),false);
        }else if(message.toLowerCase().startsWith("банк снять")){
            BFGBot.sendMessage(chat_id,bankGet(user_id,message),false);
        }else if(message.equalsIgnoreCase("Рейтинг")){
            BFGBot.sendMessage(chat_id,rating(user_id),false);
        }else if(message.toLowerCase().startsWith("продать рейтинг")){
            BFGBot.sendMessage(chat_id,sellRating(user_id,message),false);
        }else if(message.toLowerCase().startsWith("дать")){
            try {
                BFGBot.sendMessage(chat_id,giveMoney(user_id,message,update.getMessage().getReplyToMessage().getFrom().getId()),false);
            }catch (Exception e){
                BFGBot.sendMessage(chat_id,"Ошибка. Проверьте правильно введенного ID \uD83D\uDE1F",false);
            }
        }else if(message.equalsIgnoreCase("статистика")){
            BFGBot.sendMessage(chat_id,stats(),false);
        }
    }

    private static String giveMoney(long id,String text,long who){
        Users users = getUser(id);
        int how = 0;
        try {
            how = Integer.parseInt(text.substring(text.lastIndexOf(" ")).trim());
        } catch (Exception ignored) {
        }
        if(how!=0){
            if(users.money>=how){
                Users users1 = getUser(who);
                if(users1!=null){
                    users.setMoney(users.money-how);
                    users1.setMoney(users1.money+how);
                    users.save();
                    users1.save();
                    return "Вы передали "+how+"$ игроку "+setLink(who)+" \uD83D\uDE04";
                }else {
                    return "Ошибка. Проверьте правильно введенного ID \uD83D\uDE1F";
                }
            }else {
                return setLink(id)+", вы не можете передать больше чем у вас есть на балансе \uD83D\uDE1F";
            }
        }else {
            return setLink(id)+", вы не ввели сумму которую хотите передать игроку \uD83D\uDE14";
        }
    }

    private static String getTreasury(){
        long all = 0;
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                all+=resultSet.getInt("money");
                all+=resultSet.getInt("bank");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "\uD83D\uDCB0 На данный момент казна штата составляет "+all+"$";
    }



    public static String Time(long seconds,boolean details){
        int second = 0;
        int minute = 0;
        int hour = 0;
        int day = 0;
        int month = 0;
        month= (int) (TimeUnit.SECONDS.toDays(seconds)/30);
        day= (int) (TimeUnit.SECONDS.toDays(seconds)-(month*30));
        hour=(int) (TimeUnit.SECONDS.toHours(seconds)-TimeUnit.DAYS.toHours(day)-(TimeUnit.DAYS.toHours(month* 30L)));
        minute=(int) (TimeUnit.SECONDS.toMinutes(seconds)-TimeUnit.HOURS.toMinutes(hour)-TimeUnit.DAYS.toMinutes(day)-(TimeUnit.DAYS.toMinutes(month* 30L)));
        second=(int) (seconds-TimeUnit.MINUTES.toSeconds(minute)-TimeUnit.HOURS.toSeconds(hour)-TimeUnit.DAYS.toSeconds(day)-(TimeUnit.DAYS.toSeconds(month* 30L)));
        String time = "";
        if(month!=0) {
            if(month>=12){
                int year = month/12;
                month=month-(year*12);
                time = getTime(year,5)+" "+getTime(month,0);
                if(details){
                    time+=" "+getTime(day,1);
                }
            }else {
                time = getTime(month,0)+" "+getTime( day,1);
                if(details){
                    time+=" "+getTime(day,2);
                }
            }
        }else if(day!=0) {
            time = getTime( day,1)+" "+getTime(hour,2);
            if(details){
                time+=" "+getTime(minute,3);
            }
        }else if(hour!=0) {
            time = getTime(hour,2)+" "+getTime(minute,3);
            if(details){
                time+=" "+getTime(second,4);
            }
        }else if(minute!=0) {
            time = getTime(minute,3)+" "+getTime(second,4);
        }else if(second!=0) {
            time = getTime(second,4);
        }
        return time;
    }


    //0 - месяц
    //1 - день
    //2 - час
    //3 - минуты
    //4 - секунды
    //5 - год
    public static String getTime(int how,int what) {
        boolean b = String.valueOf(how).endsWith("2") || String.valueOf(how).endsWith("3") || String.valueOf(how).endsWith("4");
        if (what == 0) {
            String time = how + "";
            if (how == 1) {
                time += " месяц";
            } else if (how >= 2 && how <= 4) {
                time += " месяца";
            } else if (how > 4 && how <= 20) {
                time += " месяцев";
            } else if (String.valueOf(how).endsWith("1")) {
                time += " месяц";
            } else if (b) {
                time += " месяца";
            } else {
                time += " месяцев";
            }
            return time;
        } else if (what == 1) {
            String time = how + "";
            if (how == 1) {
                time += " день";
            } else if (how >= 2 && how <= 4) {
                time += " дня";
            } else if (how > 4 && how <= 20) {
                time += " дней";
            } else if (String.valueOf(how).endsWith("1")) {
                time += " день";
            } else if (b) {
                time += " дня";
            } else {
                time += " дней";
            }
            return time;
        } else if (what == 2) {
            String time = how + "";
            if (how == 1) {
                time += " час";
            } else if (how >= 2 && how <= 4) {
                time += " часа";
            } else if (how > 4 && how <= 20) {
                time += " часов";
            } else if (String.valueOf(how).endsWith("1")) {
                time += " час";
            } else if (b) {
                time += " часа";
            } else {
                time += " часов";
            }
            return time;
        } else if (what == 3) {
            String time = how + "";
            if (how == 1) {
                time += " минуту";
            } else if (how >= 2 && how <= 4) {
                time += " минуты";
            } else if (how > 4 && how <= 20) {
                time += " минут";
            } else if (String.valueOf(how).endsWith("1")) {
                time += " минуту";
            } else if (b) {
                time += " минуты";
            } else {
                time += " минут";
            }
            return time;
        } else if (what == 4) {
            String time = how + "";
            if (how == 1) {
                time += " секунду";
            } else if (how >= 2 && how <= 4) {
                time += " секунды";
            } else if (how > 4 && how <= 20) {
                time += " секунд";
            } else if (String.valueOf(how).endsWith("1")) {
                time += " секунду";
            } else if (b) {
                time += " секунды";
            } else {
                time += " секунд";
            }
            return time;
        } else if (what == 5) {
            String time = how + "";
            if (how == 1) {
                time += " год";
            } else if (how >= 2 && how <= 4) {
                time += " года";
            } else if (how > 4 && how <= 20) {
                time += " лет";
            } else if (String.valueOf(how).endsWith("1")) {
                time += " год";
            } else if (b) {
                time += " года";
            } else {
                time += " лет";
            }
            return time;
        } else {
            return 0 + "";
        }
    }

    private static String stats(){
        int i = 0;
        try {
            PreparedStatement statement = DBManager.c.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "\uD83D\uDC64 Всего пользователей в боте "+i;
    }
}
