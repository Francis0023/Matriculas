import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class principal {
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEdad;
    private JTextField txtCurso;
    private JTextField txtIngreso;
    private JTextField TFoto;
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

    private Connection connection;

    public principal() {

        // Conexión a la base de datos
        connectToDatabase();

        // Configuración de la tabla
        configureTable();

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
            String password = "f123456";
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
            model.addColumn("ID");
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
                int id = resultSet.getInt("id");
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

                model.addRow(new Object[]{id,cedula,nombre,apellido,direccion,telefono,edad,curso,fecha_matricula,periodo_ciclo,foto});
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
            String curso = txtCurso.getText();
            //Date fecha_matricula =
            String fecha_matricula = txtIngreso.getText();
            //Blob foto =
            String foto = TFoto.getText();

            // Verificar que el curso ingresado sea válido
            if (!cursoValido(curso)) {
                JOptionPane.showMessageDialog(null, "El curso ingresado no es válido. Seleccione entre 'Base de Datos', 'Redes I' o 'Fundamentos de Programación'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insertar datos en la base de datos
            String insertQuery = "INSERT INTO Estudiantes(cedula,nombre,apellido,direccion,telefono,edad,curso,fecha_matricula,foto)values\n" +
                    "(?,?,?,?,?,?,?,?,?);";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1,cedula);
            insertStatement.setString(2, nombre);
            insertStatement.setString(3, apellido);
            insertStatement.setString(4, direccion);
            insertStatement.setString(5, telefono);
            insertStatement.setInt(6,edad);
            insertStatement.setString(7, curso);
            insertStatement.setString(8, fecha_matricula);
            insertStatement.setString(9, foto);


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
            txtCurso.setText("");
            txtIngreso.setText("");
            TFoto.setText("");

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
                && !txtTelefono.getText().isEmpty() && !txtEdad.getText().isEmpty() && !txtCurso.getText().isEmpty() && !txtIngreso.getText().isEmpty() && !TFoto.getText().isEmpty();
    }

    private boolean cursoValido(String curso) {
        return curso.equals("Base de Datos") || curso.equals("Redes I") || curso.equals("Fundamentos de Programación");
    }
    private void buscarDatos() {
        try {
            // Obtener el ID ingresado
            String idInput = JOptionPane.showInputDialog(this.matricula, "Ingrese el ID del estudiante que desea buscar:", "Buscar por ID", JOptionPane.QUESTION_MESSAGE);

            // Verificar si se ingresó un ID
            if (idInput != null && !idInput.isEmpty()) {
                int id = Integer.parseInt(idInput);

                // Consultar la existencia del ID en la base de datos
                if (existeestudiantes(id)) {
                    // Obtener la información del estudiante por ID
                    String query = "SELECT * FROM Estudiantes WHERE id=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, id);
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
                                String foto = resultSet.getString("foto");

                                // Mostrar la información en los campos de texto
                                txtCedula.setText(String.valueOf(cedula));
                                txtNombre.setText(nombre);
                                txtApellido.setText(apellido);
                                txtDireccion.setText(direccion);
                                txtTelefono.setText(telefono);
                                txtEdad.setText(String.valueOf(edad));
                                txtCurso.setText(curso);
                                txtIngreso.setText(fecha_matricula);
                                TFoto.setText(foto);

                                // Mostrar mensaje de éxito
                                String mensaje = "ID: " + id + "\nCedula: " + cedula + "\nNombre: " + nombre + "\nApellido: " + apellido
                                        + "\nDireccion: " + direccion + "\nTelefono: " + telefono + "\nEdad: " + edad
                                        + "\nCurso: " + curso + "\nFecha Matricula: " + fecha_matricula + "\nFoto: " + foto;
                                JOptionPane.showMessageDialog(this.matricula, mensaje, "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this.matricula, "El estudiante con ID " + id + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarDatos() {
        try {
            // Obtener el ID ingresado
            String idInput = JOptionPane.showInputDialog(this.matricula, "Ingrese el ID del estudiante que desea actualizar:", "Actualizar por ID", JOptionPane.QUESTION_MESSAGE);

            // Verificar si se ingresó un ID
            if (idInput != null && !idInput.isEmpty()) {
                int id = Integer.parseInt(idInput);

                // Consultar la existencia del ID en la base de datos
                if (existeestudiantes(id)) {
                    // Obtener los nuevos valores de los campos de texto
                    int cedula = Integer.parseInt(txtCedula.getText());
                    String nombre = txtNombre.getText();
                    String apellido = txtApellido.getText();
                    String direccion = txtDireccion.getText();
                    String telefono = txtTelefono.getText();
                    int edad = Integer.parseInt(txtEdad.getText());
                    String curso = txtCurso.getText();
                    String fecha_matricula = txtIngreso.getText();
                    String foto = TFoto.getText();

                    // Actualizar el estudiante en la base de datos por ID
                    String query = "UPDATE Estudiantes SET cedula=?, nombre=?, apellido=?, direccion=?, telefono=?, edad=?, curso=?, fecha_matricula=?, foto=? WHERE id=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, cedula);
                        preparedStatement.setString(2, nombre);
                        preparedStatement.setString(3, apellido);
                        preparedStatement.setString(4, direccion);
                        preparedStatement.setString(5, telefono);
                        preparedStatement.setInt(6, edad);
                        preparedStatement.setString(7, curso);
                        preparedStatement.setString(8, fecha_matricula);
                        preparedStatement.setString(9, foto);
                        preparedStatement.setInt(10, id);

                        preparedStatement.executeUpdate();
                    }

                    // Limpiar campos de texto
                    txtCedula.setText("");
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtDireccion.setText("");
                    txtTelefono.setText("");
                    txtEdad.setText("");
                    txtCurso.setText("");
                    txtIngreso.setText("");
                    TFoto.setText("");

                    // Recargar datos en la tabla
                    configureTable();
                } else {
                    JOptionPane.showMessageDialog(this.matricula, "El estudiante con ID " + id + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private boolean existeestudiantes(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM Estudiantes WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
    }

    private void borrarDatos() {
        try {
            // Obtener el ID ingresado
            String idInput = JOptionPane.showInputDialog(this.matricula, "Ingrese el ID del estudiante que desea borrar:", "Borrar por ID", JOptionPane.QUESTION_MESSAGE);

            // Verificar si se ingresó un ID
            if (idInput != null && !idInput.isEmpty()) {
                int id = Integer.parseInt(idInput);

                // Consultar la existencia del ID en la base de datos
                if (existeestudiantes(id)) {
                    // Borrar el estudiante de la base de datos por ID
                    String query = "DELETE FROM Estudiantes WHERE id=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeUpdate();
                    }

                    // Limpiar campos de texto
                    txtCedula.setText("");
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtDireccion.setText("");
                    txtTelefono.setText("");
                    txtEdad.setText("");
                    txtCurso.setText("");
                    txtIngreso.setText("");
                    TFoto.setText("");

                    // Recargar datos en la tabla
                    configureTable();

                    // Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(this.matricula, "Estudiante con ID " + id + " borrado correctamente.", "Borrado exitoso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this.matricula, "El estudiante con ID " + id + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
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
            TFoto.setText(imagePath);
            // Limpiar la visualización de la imagen al seleccionar una nueva
            imagenLabel.setIcon(null);
        }
    }
    // Método para visualizar la imagen seleccionada
    private void visualizarFoto() {
        String imagePath = TFoto.getText();
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
}
