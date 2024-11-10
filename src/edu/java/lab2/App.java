package edu.java.lab2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Программа для управления списком фильмов в кинотеатре
 * 
 * Author: Ivan_Arno
 * Version: 1.1
 * Since: 2024
 */

public class App {
    private JFrame frame;
    private JToolBar buttonsPanel;
    private JButton save;
    private JButton open;
    private JButton add;
    private JButton edit;
    private JButton delete;
    private JButton info;
    private JButton filter;
    private DefaultTableModel model;
    private JTable films;
    private JComboBox<String> name;
    private JTextField filmName;
    private JPanel filterPanel;
    
    // Основной метод для отображения окна приложения
    public void show() {
        setupFrame();
        setupToolbar();
        setupTable();
        setupSearchPanel();
        setupEventHandlers();
        
        // Отображение окна
        frame.setVisible(true);
    }

    // Настройка основного окна
    private void setupFrame() {
        frame = new JFrame("Список фильмов");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
    }

    // Настройка панели инструментов с кнопками
    private void setupToolbar() {
        buttonsPanel = new JToolBar();
        
        // Создание кнопок с иконками и подсказками
        save = new JButton(new ImageIcon("./Icons/save2.png"));
        save.setToolTipText("Сохранить список фильмов");
        
        open = new JButton(new ImageIcon("./Icons/Open.png"));
        open.setToolTipText("Открыть список фильмов");
        
        add = new JButton(new ImageIcon("./Icons/add.png"));
        add.setToolTipText("Добавить новый фильм");
        
        edit = new JButton(new ImageIcon("./Icons/edit.png"));
        edit.setToolTipText("Редактировать выбранный фильм");
        
        delete = new JButton(new ImageIcon("./Icons/trash.png"));
        delete.setToolTipText("Удалить выбранный фильм");
        
        info = new JButton(new ImageIcon("./Icons/info.png"));
        info.setToolTipText("Посмотреть информацию о выбранном фильме");

        // Добавление кнопок на панель
        buttonsPanel.add(save);
        buttonsPanel.add(open);
        buttonsPanel.add(add);
        buttonsPanel.add(edit);
        buttonsPanel.add(delete);
        buttonsPanel.add(info);
        
        // Добавление панели инструментов в верхнюю часть окна
        frame.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
    }

    // Настройка таблицы для отображения данных о фильмах
    private void setupTable() {
        String[] columns = { "Фильм", "Жанр", "Сеанс", "Проданные билеты", "Режиссер", "Год", "Студия" };
        Object[][] data = {
            {"Дюна", "Научная фантастика", "18:00", "120", "Дени Вильнёв", "2021", "Legendary Pictures"},
            {"Темные времена", "Драма", "20:30", "90", "Джо Райт", "2017", "Working Title Films"}
        };
        
        model = new DefaultTableModel(data, columns);
        films = new JTable(model);
        
        // Добавление таблицы с прокруткой в центр окна
        frame.add(BorderLayout.CENTER, new JScrollPane(films));
    }

    // Настройка панели поиска
    private void setupSearchPanel() {
        name = new JComboBox<>(new String[] { "Фильм", "Жанр", "Сеанс" });
        filmName = new JTextField("Название фильма");
        filter = new JButton("Поиск");
        
        filterPanel = new JPanel();
        filterPanel.add(name);
        filterPanel.add(filmName);
        filterPanel.add(filter);
        
        // Добавление панели поиска в нижнюю часть окна
        frame.add(BorderLayout.SOUTH, filterPanel);
    }

    // Настройка обработчиков событий
    private void setupEventHandlers() {
        ButtonListener buttonListener = new ButtonListener();
        
        save.setActionCommand("Сохранить");
        open.setActionCommand("Открыть");
        info.setActionCommand("Информация");
        add.setActionCommand("Добавить");
        delete.setActionCommand("Удалить");
        edit.setActionCommand("Редактировать");
        
        save.addActionListener(buttonListener);
        open.addActionListener(buttonListener);
        info.addActionListener(buttonListener);
        add.addActionListener(buttonListener);
        delete.addActionListener(buttonListener);
        edit.addActionListener(buttonListener);
        
        // Обработчик для кнопки "Поиск"
        filter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    checkName(filmName);
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(frame, ex.toString());
                } catch (MyException myEx) {
                    JOptionPane.showMessageDialog(null, myEx.getMessage());
                }
            }
        });
        
        // Обработчик фокуса для поля "Название фильма"
        filmName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (filmName.getText().equals("Название фильма")) {
                    filmName.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (filmName.getText().isEmpty()) {
                    filmName.setText("Название фильма");
                }
            }
        });
    }

    // Обработчик событий кнопок
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
            	case "Сохранить":
            		//saveFilmList();
            		exportToXML();
            		break;
            	case "Открыть":
            		//openFilmList();
            		readXML();
            		break;
                case "Информация":
                    showFilmInfo();
                    break;
                case "Редактировать":
                    editFilm();
                    break;
                case "Добавить":
                    model.addRow(new Object[]{"", "", "", "", "", "", ""});
                    break;
                case "Удалить":
                    deleteFilm();
                    break;
            }
        }
        
        //Метод для сохранения списка фильмов
        private void saveFilmList() {
        	FileDialog save = new FileDialog(frame, "Сохранение данных", FileDialog.SAVE);
        	save.setFile("*.txt");
        	save.setVisible(true); // Отобразить запрос пользователю
        	// Определить имя выбранного каталога и файла
        	String fileName = save.getDirectory() + save.getFile();
        	if (fileName == null) return; // Если пользователь нажал «отмена»
        	try {
        		BufferedWriter writer = new BufferedWriter (new FileWriter(fileName));
        		for (int i = 0; i < model.getRowCount(); i++) // Для всех строк
        		for (int j = 0; j < model.getColumnCount(); j++) // Для всех столбцов
        		{writer.write ((String) model.getValueAt(i, j)); // Записать значение из ячейки
        		writer.write("\n"); // Записать символ перевода каретки
        		}
        		writer.close();
        		 }
        		catch(IOException e) // Ошибка записи в файл
        		{ e.printStackTrace(); }
        }
        
      //Метод для открытия списка фильмов
        private void openFilmList() {
        	FileDialog open = new FileDialog(frame, "Открытие данных", FileDialog.LOAD);
        	open.setFile("*.txt");
        	open.setVisible(true); // Отобразить запрос пользователю
        	// Определить имя выбранного каталога и файла
        	String fileName = open.getDirectory() + open.getFile();
        	if(fileName == null) return; // Если пользователь нажал «отмена»
        	try {
        		BufferedReader reader = new BufferedReader(new FileReader(fileName));
        		int rows = model.getRowCount();
        		for (int i = 0; i < rows; i++) model.removeRow(0); // Очистка таблицы
        		String film;
        		do {
        		film = reader.readLine();
        		if(film != null)
        		{ String genre = reader.readLine();
        		String session = reader.readLine();
        		String ticketsSold = reader.readLine();
        		String director = reader.readLine();
        		String year = reader.readLine();
        		String studion = reader.readLine();
        		model.addRow(new String[]{film, genre, session, ticketsSold, director, year, studion}); // Запись строки в таблицу
        		}
        		} while(film != null);
        		reader.close();
        		} catch (FileNotFoundException e) {e.printStackTrace();} // файл не найден
        		 catch (IOException e) {e.printStackTrace();}

        }

        // Метод для показа информации о фильме
        private void showFilmInfo() {
            int selectedRow = films.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите фильм для просмотра информации.");
                return;
            }

            String filmName = (String) model.getValueAt(selectedRow, 0);
            String genre = (String) model.getValueAt(selectedRow, 1);
            String session = (String) model.getValueAt(selectedRow, 2);
            String ticketsSold = (String) model.getValueAt(selectedRow, 3);
            String director = (String) model.getValueAt(selectedRow, 4);
            String year = (String) model.getValueAt(selectedRow, 5);
            String studio = (String) model.getValueAt(selectedRow, 6);

            Object[] message = {
                "Название фильма: " + filmName,
                "Жанр: " + genre,
                "Сеанс: " + session,
                "Проданные билеты: " + ticketsSold,
                "Режиссер: " + director,
                "Год выхода: " + year,
                "Студия: " + studio
            };

            JOptionPane.showMessageDialog(frame, message, "Информация о фильме", JOptionPane.INFORMATION_MESSAGE);
        }

        // Метод для редактирования фильма
        private void editFilm() {
            int selectedRow = films.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите строку для редактирования.");
                return;
            }

            JTextField filmNameInput = new JTextField((String) model.getValueAt(selectedRow, 0));
            JTextField genreInput = new JTextField((String) model.getValueAt(selectedRow, 1));
            JTextField sessionInput = new JTextField((String) model.getValueAt(selectedRow, 2));
            JTextField ticketsInput = new JTextField((String) model.getValueAt(selectedRow, 3));
            JTextField directorInput = new JTextField((String) model.getValueAt(selectedRow, 4));
            JTextField yearInput = new JTextField((String) model.getValueAt(selectedRow, 5));
            JTextField studioInput = new JTextField((String) model.getValueAt(selectedRow, 6));

            Object[] message = {
                "Название фильма:", filmNameInput,
                "Жанр:", genreInput,
                "Сеанс:", sessionInput,
                "Проданные билеты:", ticketsInput,
                "Режиссер:", directorInput,
                "Год выхода:", yearInput,
                "Студия:", studioInput
            };

            int option = JOptionPane.showConfirmDialog(frame, message, "Редактировать фильм", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                model.setValueAt(filmNameInput.getText(), selectedRow, 0);
                model.setValueAt(genreInput.getText(), selectedRow, 1);
                model.setValueAt(sessionInput.getText(), selectedRow, 2);
                model.setValueAt(ticketsInput.getText(), selectedRow, 3);
                model.setValueAt(directorInput.getText(), selectedRow, 4);
                model.setValueAt(yearInput.getText(), selectedRow, 5);
                model.setValueAt(studioInput.getText(), selectedRow, 6);
            }
        }

        // Метод для удаления фильма
        private void deleteFilm() {
            int selectedRow = films.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Не выбрана строка для удаления. Пожалуйста, выберите строку.");
            } else {
                model.removeRow(selectedRow);
            }
        }
    }

    // Проверка корректности введенного названия фильма
    private void checkName(JTextField bName) throws MyException, NullPointerException {
        String sName = bName.getText();
        if (sName.contains("Название фильма")) throw new MyException();
        if (sName.length() == 0) throw new NullPointerException();
    }

    // Кастомное исключение для пустого названия фильма
    private class MyException extends Exception {
        public MyException() {
            super("Вы не ввели название фильма для поиска.");
        }
    }
    
    //XML документ
    private void exportToXML() {
        Document doc = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        
        // Создаем корневой элемент и добавляем его
        Element filmlist = doc.createElement("filmlist");
        doc.appendChild(filmlist);
        
        // Создаем элементы "film" для каждой строки в таблице
        for (int i = 0; i < model.getRowCount(); i++) {
            Element film = doc.createElement("film");
            film.setAttribute("name", (String) model.getValueAt(i, 0));
            film.setAttribute("genre", (String) model.getValueAt(i, 1));
            film.setAttribute("session", (String) model.getValueAt(i, 2));
            film.setAttribute("ticketsSold", (String) model.getValueAt(i, 3));
            film.setAttribute("director", (String) model.getValueAt(i, 4));
            film.setAttribute("year", (String) model.getValueAt(i, 5));
            film.setAttribute("studio", (String) model.getValueAt(i, 6));
            filmlist.appendChild(film);
        }
        
        try {
            // Настройка преобразователя и запись в файл
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            
            FileWriter writer = new FileWriter("films.xml");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            writer.close();
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
    }
    
   //Чтение XML
    private void readXML() {
    	Document doc = null;
    	try {
    		// Создание парсера документа
    		DocumentBuilder dBuilder =
    		DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		// Чтение документа из файла
    		doc = dBuilder.parse(new File("films.xml"));
    		// Нормализация документа
    		doc.getDocumentElement().normalize();
    		}
    		catch (ParserConfigurationException e) { e.printStackTrace(); }
    		// Обработка ошибки парсера при чтении данных из XML-файла
    		catch (SAXException e) { e.printStackTrace(); }
    		catch (IOException e) { e.printStackTrace(); }
    		// Получение списка элементов с именем book
    		 NodeList nlfilms = doc.getElementsByTagName("film");
    		// Цикл просмотра списка элементов и запись данных в таблицу
    		 for (int temp = 0; temp < nlfilms.getLength(); temp++) {
    		// Выбор очередного элемента списка
    		Node elem = nlfilms.item(temp);
    		// Получение списка атрибутов элемента
    		NamedNodeMap attrs = elem.getAttributes();
    		// Чтение атрибутов элемента
    		String name = attrs.getNamedItem("name").getNodeValue();
    		String genre = attrs.getNamedItem("genre").getNodeValue();
    		String session = attrs.getNamedItem("session").getNodeValue();
    		String ticketsSold = attrs.getNamedItem("ticketsSold").getNodeValue();
    		String director = attrs.getNamedItem("director").getNodeValue();
    		String year = attrs.getNamedItem("year").getNodeValue();
    		String studio = attrs.getNamedItem("studio").getNodeValue();
    		// Запись данных в таблицу
    		model.addRow(new String[]{name, genre, session, ticketsSold, director, year, studio});
    		}
    }


    public static void main(String[] args) {
        new App().show();
    }
}

