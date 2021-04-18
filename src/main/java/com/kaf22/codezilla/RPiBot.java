package com.kaf22.codezilla;

import com.pi4j.io.gpio.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RPiBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(RPiBot.class);
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    @Override
    public String getBotUsername() {
        return "Open the dor bot";
    }

    @Override
    public String getBotToken() {
        return "1753334564:AAEDxWT7OGFX5zmQ_x-rVFmX_b0Sp57OJ7I";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        String chatId = update.getMessage().getChatId().toString();
        String inputText = update.getMessage().getText();

        if (inputText.startsWith("/start")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Open the dor");
            keyboardFirstRow.add("Information");
            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Welcome to Codezilla Bot");
            message.setReplyMarkup(replyKeyboardMarkup);


            execute(message);
        } else if (inputText.equals("Information")) {
            BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("vcgencmd measure_temp").getInputStream()));

            String line;

            SendMessage message = new SendMessage();
            message.setChatId(chatId);

            while ((line = br.readLine()) != null){
                log.info(line);
                message.setText(line + "\nDate: " + LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonth() + "." + LocalDate.now().getYear());
            }

            execute(message);

        } else if (inputText.equals("Open the door")) {
            GpioController gpio = GpioFactory.getInstance();

            Pin redLedPin = RaspiPin.GPIO_21;
            Pin greenLedPin = RaspiPin.GPIO_10;
            Pin buttonPin = RaspiPin.GPIO_06;

            GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(redLedPin, "Read", PinState.LOW);
            pin.high();


        }
    }
}
