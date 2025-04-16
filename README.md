
# **Calculadora de Subredes VLSM para Android**

## **Descripción**  
Esta aplicación para Android permite realizar cálculos de subredes utilizando el método **VLSM (Variable Length Subnet Masking)**. Es una herramienta diseñada para ayudar a estudiantes, técnicos y profesionales en redes a planificar y dividir redes IP de manera eficiente, asignando el espacio exacto necesario para cada subred, basado en los requerimientos de hosts específicos.

## **Características**  
- Cálculo de subredes VLSM desde una dirección IP base  
- Soporte para diferentes prefijos **CIDR (Classless Inter-Domain Routing)**  
- Creación dinámica de subredes según sea necesario  
- Especificación precisa de la cantidad de hosts requeridos por subred  
- Visualización de resultados de forma clara y en formato de tabla  
- Cálculo automático de:  
  - Dirección IP de la subred  
  - Prefijo CIDR de cada subred  
  - Rango de direcciones IP utilizables  
  - Dirección de broadcast  
  - Número de hosts disponibles frente a los requeridos

## **Requisitos**  
- **Android 5.0 (API nivel 21)** o superior  
- **Java 8+**  
- **Android Studio 4.0+**

## **Instalación**  
1. Clona el repositorio:  
   ```bash
   git clone https://github.com/Antho-321/Calculadora_VLSM.git
   ```
2. Abre el proyecto en **Android Studio**:  
   - Selecciona "Open an existing Android Studio project"  
   - Navega a la carpeta del proyecto y selecciona "OK"
3. Compila y ejecuta la aplicación:  
   - Selecciona "Run" > "Run 'app'" en Android Studio  
   - Elige un dispositivo o emulador para ejecutar la aplicación

## **Uso de la aplicación**  
- **Dirección IP Base**: Ingresa la dirección de red base (ej. `192.168.1.0`)  
- **Prefijo CIDR**: Elige el prefijo CIDR para la red base (ej. `/24`)  
- **Número de subredes**: Indica cuántas subredes deseas crear  
- **Hosts por subred**: Para cada subred, especifica el número de hosts requeridos  
- **Calcular**: Pulsa el botón "Calcular Subredes" para obtener los resultados  
- **Resultados**: Una tabla mostrará la información de cada subred generada

## **Estructura del Proyecto**  
```plaintext
ec/edu/utn/example/calculadora_vlsm/
├── MainActivity.java         # Controlador principal y UI  
├── VLSMCalculator.java       # Lógica del cálculo VLSM  
└── UIValidator.java          # Validación de entradas  

res/layout/
├── activity_main.xml         # Layout principal  
└── subnet_results_layout.xml # Layout para los resultados
```

## **Componentes Clave**  
- **MainActivity**: Maneja la interfaz de usuario e interacciones. Incluye:  
  - Gestión dinámica de campos de entrada para hosts  
  - Validación de entradas  
  - Presentación de los resultados  
- **VLSMCalculator**: Implementa la lógica para calcular las subredes VLSM:  
  - Conversión entre la notación IP decimal y binaria  
  - Cálculo del tamaño óptimo de subred para los hosts requeridos  
  - Asignación de direcciones IP a las subredes  
  - Cálculo de rangos de dirección y de broadcast  
- **UIValidator**: Proporciona funciones para validar:  
  - Direcciones IPv4  
  - Valores CIDR  
  - Cantidad de subredes y hosts  

## **Algoritmo VLSM**  
El algoritmo sigue los siguientes pasos:  
1. Ordena las subredes por número de hosts (de mayor a menor)  
2. Para cada subred:  
   - Calcula el prefijo CIDR mínimo necesario  
   - Asigna direcciones IP desde el espacio disponible  
   - Calcula la dirección de broadcast y el rango utilizable  
3. Verifica que todas las subredes encajen dentro de la red base

## **Contribuciones**  
Las contribuciones son bienvenidas. Para colaborar, sigue estos pasos:  
1. Haz un fork del proyecto  
2. Crea una rama para tu nueva característica  
   ```bash
   git checkout -b feature/nueva-caracteristica
   ```
3. Realiza un commit con los cambios  
   ```bash
   git commit -m 'Añadir nueva característica'
   ```
4. Haz push a la rama  
   ```bash
   git push origin feature/nueva-caracteristica
   ```
5. Abre un Pull Request

## **Licencia**  
Este proyecto está licenciado bajo la **Licencia MIT**. Consulta el archivo LICENSE para más detalles.

## **Contacto**  
Si tienes preguntas o sugerencias, por favor abre un **issue** en el repositorio.
