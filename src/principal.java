import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;

public class principal {
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEdad;

    private JButton borrarButton;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton buscarButton;
    private JButton buscarFotoButton;
    private JButton vizualizarFotoButton;
    private JTable table1;
    private JScrollPane tableModel;
    JPanel matricula;
    private JLabel imagenLabel;
    private JComboBox cursoComboBox;

    private Connection connection;
    String foto_path = "";
    public principal() {

        // Conexión a la base de datos
        connectToDatabase();

        // Configuración de la tabla
        configureTable();

        String[] cursos = {"Base de Datos", "Redes I", "Fundamentos de Programación"};
        cursoComboBox.addItem(cursos[0]);
        cursoComboBox.addItem(cursos[1]);
        cursoComboBox.addItem(cursos[2]);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camposLlenos()) {
                    agregarDatos();
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarDatos();
            }
        });
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarDatos();
            }
        });
        borrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrarDatos();
            }
        });
        buscarFotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarFoto();
            }
        });
        vizualizarFotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizarFoto();
            }
        });
    }
    private void connectToDatabase() {
        try {
            // Conexión a la base de datos
            String url = "jdbc:mysql://localhost:3306/matriculacion";
            String user = "root";
            String password = "123456";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void configureTable() {
        try {
            // Obtener datos de la base de datos y configurar la tabla
            String query = "SELECT * FROM Estudiantes";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Crear un modelo de tabla para almacenar los datos
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Cedula");
            model.addColumn("Nombre");
            model.addColumn("Apellido");
            model.addColumn("Direccion");
            model.addColumn("Telefono");
            model.addColumn("Edad");
            model.addColumn("Curso");
            model.addColumn("Fecha");
            model.addColumn("Periodo");
            model.addColumn("Foto");

            // Llenar el modelo con los datos de la base de datos
            while (resultSet.next()) {
                int cedula = resultSet.getInt("cedula");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                String direccion = resultSet.getString("direccion");
                String telefono = resultSet.getString("telefono");
                int edad = resultSet.getInt("edad");
                String curso = resultSet.getString("curso");
                //Fecha
                String fecha_matricula = resultSet.getString("fecha_matricula");
                String periodo_ciclo = resultSet.getString("periodo_ciclo");
                //Foto
                String foto = resultSet.getString("foto");

                model.addRow(new Object[]{cedula,nombre,apellido,direccion,telefono,edad,curso,fecha_matricula,periodo_ciclo,foto});
            }

            // Configurar la tabla con el modelo
            table1.setModel(model);

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void agregarDatos() {
        try {
            // Obtener los valores ingresados por el usuario
            int cedula = Integer.parseInt(txtCedula.getText());
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String direccion = txtDireccion.getText();
            String telefono = txtTelefono.getText();
            int edad = Integer.parseInt(txtEdad.getText());
            String curso = (String) cursoComboBox.getSelectedItem();
            //Blob foto =
            byte[] foto = foto_en_bytes(foto_path);

            // Verificar que el curso ingresado sea válido
            if (!cursoValido(curso)) {
                JOptionPane.showMessageDialog(null, "El curso ingresado no es válido. Seleccione entre 'Base de Datos', 'Redes I' o 'Fundamentos de Programación'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insertar datos en la base de datos
            String insertQuery = "INSERT INTO Estudiantes(cedula,nombre,apellido,direccion,telefono,edad,curso,foto)values\n" +
                    "(?,?,?,?,?,?,?,?);";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1,cedula);
            insertStatement.setString(2, nombre);
            insertStatement.setString(3, apellido);
            insertStatement.setString(4, direccion);
            insertStatement.setString(5, telefono);
            insertStatement.setInt(6,edad);
            insertStatement.setString(7, curso);
            insertStatement.setBytes(8, foto);


            insertStatement.executeUpdate();

            // Actualizar la tabla
            configureTable();

            // Limpiar los campos de entrada después de la inserción
            txtCedula.setText("");
            txtNombre.setText("");
            txtApellido.setText("");
            txtDireccion.setText("");
            txtTelefono.setText("");
            txtEdad.setText("");


            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(null, "Datos agregados correctamente", "Correcto", JOptionPane.INFORMATION_MESSAGE);

            // Cerrar recursos
            insertStatement.close();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al agregar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean camposLlenos() {
        return !txtNombre.getText().isEmpty() && !txtApellido.getText().isEmpty() && !txtDireccion.getText().isEmpty()
                && !txtTelefono.getText().isEmpty() && !txtEdad.getText().isEmpty();
    }

    private boolean cursoValido(String curso) {
        return curso.equals("Base de Datos") || curso.equals("Redes I") || curso.equals("Fundamentos de Programación");
    }
    private void buscarDatos() {
        try {
            // Obtener el cedula ingresado
            String idInput = JOptionPane.showInputDialog(this.matricula, "Ingrese el Cedula del estudiante que desea buscar:", "Buscar por cedula", JOptionPane.QUESTION_MESSAGE);

            // Verificar si se ingresó un cedula
            if (idInput != null && !idInput.isEmpty()) {
                int ncedula = Integer.parseInt(idInput);

                // Consultar la existencia del cedula en la base de datos
                if (existeestudiantes(ncedula)) {
                    // Obtener la información del estudiante por cedula
                    String query = "SELECT * FROM Estudiantes WHERE cedula=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, ncedula);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            // Mostrar la información en los campos de texto
                            if (resultSet.next()) {
                                int cedula = resultSet.getInt("cedula");
                                String nombre = resultSet.getString("nombre");
                                String apellido = resultSet.getString("apellido");
                                String direccion = resultSet.getString("direccion");
                                String telefono = resultSet.getString("telefono");
                                int edad = resultSet.getInt("edad");
                                String curso = resultSet.getString("curso");
                                String fecha_matricula = resultSet.getString("fecha_matricula");
                                byte[] foto = resultSet.getBytes("foto");

                                // Mostrar la información en los campos de texto
                                txtCedula.setText(String.valueOf(cedula));
                                txtNombre.setText(nombre);
                                txtApellido.setText(apellido);
                                txtDireccion.setText(direccion);
                                txtTelefono.setText(telefono);
                                txtEdad.setText(String.valueOf(edad));
                                cursoComboBox.setSelectedItem(curso);

                                ImageIcon imageIcon = new ImageIcon(foto);
                                Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                ImageIcon scaledIcon = new ImageIcon(image);
                                String mensaje = "\nCedula: " + cedula + "\nNombre: " + nombre + "\nApellido: " + apellido
                                            + "\nDireccion: " + direccion + "\nTelefono: " + telefono + "\nEdad: " + edad
                                            + "\nCurso: " + curso + "\nFecha Matricula: " + fecha_matricula ;
                                JOptionPane.showMessageDialog(this.matricula, mensaje, "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE, scaledIcon);

                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this.matricula, "El estudiante con cedula " + ncedula + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarDatos() {
        try {
            // Obtener el cedula ingresado
                int cedula = Integer.parseInt(txtCedula.getText());

                // Consultar la existencia del cedula en la base de datos
                if (existeestudiantes(cedula)) {
                    // Obtener los nuevos valores de los campos de texto
                    String nombre = txtNombre.getText();
                    String apellido = txtApellido.getText();
                    String direccion = txtDireccion.getText();
                    String telefono = txtTelefono.getText();
                    int edad = Integer.parseInt(txtEdad.getText());
                    String curso = (String) cursoComboBox.getSelectedItem();
                    byte[] foto = foto_en_bytes(foto_path);

                    // Actualizar el estudiante en la base de datos por cedula
                    String query = "UPDATE Estudiantes SET  nombre=?, apellido=?, direccion=?, telefono=?, edad=?, curso=?, foto=? WHERE cedula=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(8, cedula);
                        preparedStatement.setString(1, nombre);
                        preparedStatement.setString(2, apellido);
                        preparedStatement.setString(3, direccion);
                        preparedStatement.setString(4, telefono);
                        preparedStatement.setInt(5, edad);
                        preparedStatement.setString(6, curso);
                        preparedStatement.setBytes(7, foto);

                        preparedStatement.executeUpdate();
                    }

                    // Limpiar campos de texto
                    txtCedula.setText("");
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtDireccion.setText("");
                    txtTelefono.setText("");
                    txtEdad.setText("");


                    // Recargar datos en la tabla
                    configureTable();
                } else {
                    JOptionPane.showMessageDialog(this.matricula, "El estudiante con cedula " + cedula + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private boolean existeestudiantes(int cedula) throws SQLException {
        String query = "SELECT COUNT(*) FROM Estudiantes WHERE cedula=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cedula);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
    }

    private void borrarDatos() {
        try {
            // Obtener el cedula ingresado
            String idInput = JOptionPane.showInputDialog(this.matricula, "Ingrese el cedula del estudiante que desea borrar:", "Borrar por cedula", JOptionPane.QUESTION_MESSAGE);

            // Verificar si se ingresó un cedula
            if (idInput != null && !idInput.isEmpty()) {
                int cedula = Integer.parseInt(idInput);

                // Consultar la existencia del cedula en la base de datos
                if (existeestudiantes(cedula)) {
                    // Borrar el estudiante de la base de datos por cedula
                    String query = "DELETE FROM Estudiantes WHERE cedula=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, cedula);
                        preparedStatement.executeUpdate();
                    }

                    // Limpiar campos de texto
                    txtCedula.setText("");
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtDireccion.setText("");
                    txtTelefono.setText("");
                    txtEdad.setText("");

                    // Recargar datos en la tabla
                    configureTable();

                    // Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(this.matricula, "Estudiante con cedula " + cedula + " borrado correctamente.", "Borrado exitoso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this.matricula, "El estudiante con cedula " + cedula + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }
    // Método para buscar y seleccionar una imagen
    private void buscarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Imagen");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();
            foto_path = imagePath;
            // Limpiar la visualización de la imagen al seleccionar una nueva
            imagenLabel.setIcon(null);
        }
    }
    // Método para visualizar la imagen seleccionada
    private void visualizarFoto() {
        String imagePath = foto_path;
        if (!imagePath.isEmpty()) {
            try {
                ImageIcon imageIcon = new ImageIcon(imagePath);
                // Ajustar el tamaño de la imagen para que se ajuste al JLabel
                Image image = imageIcon.getImage().getScaledInstance(imagenLabel.getWidth(), imagenLabel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(image);
                imagenLabel.setIcon(scaledIcon);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al cargar y visualizar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una imagen primero", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private byte[] foto_en_bytes(String path){
        File file = new File(path);
        byte[] resultado = new byte[0];
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            resultado = bos.toByteArray();

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return resultado;
    }
}

