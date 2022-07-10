---
title: Rest Api
---

# Introducción

La API REST permite obtener los productos desde la base de datos filtrados y paginados por cada 8 registros, de igual manera devuelve la cantidad de produtos que existen con el filtro que se haya indicado en la petición.

En la documentación general se explicará el uso, construcción, tecnologías, deployment y más de la api con el fin de que se pueda comprender en integridad la aplicación.


# Construcción

## Spring Boot

Para construir la api se hizo uso del framework de Java Spring Boot, este framework está altamente calificado debido a su rendimiento, seguridad entre otros puntos.

## Dependencias

### Spring Data JPA

Se usó Spring Data JPA como dependcia para consumir datos de la base de datos a través de interfaces. 

```
    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
```

### Spring Web

Se usó Spring Web como soporte para el desarrollo web de api rest. 

```
    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
    </dependency>
```

### Mysql Driver

Para permitir la conexión a la base de datos en Mysql. 

```
    <dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
    </dependency>
```

### Lombok

Se usó lombok para la reducción y optimización de escritura de código gracias a las anotaciones que permiten generar setters, getters, constructores entre otros. 

```
    <dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
    </dependency>
```

### Spring Boot Cache

Para el cacheado de repositorios y entidades.

```
    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
```

## Conexión a la base de datos Mysql

Para la conexión a la base de datos se remota se hizo uso de la siguiente configuración en aplication.properties. Indicando tanto el servidor, nombre de la base de datos, driver a usar y credenciales de acceso.

```
    #Conexion a la base de datos

    #Driver para la conexion
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    #Url host y nombre de la base de datos
    spring.datasource.url=jdbc:mysql://mdb-test.c6vunyturrl6.us-west-1.rds.amazonaws.com/bsale_test

    #Credenciales de la base de datos
    spring.datasource.username=bsale_test
    spring.datasource.password=bsale_test

    #Mostrar queries ejecutadas en la base datos
    spring.jpa.show-sql = true
```

## Entidades

La primera entidad generada fue Category, en el cual se indica que el identificador debe ser el campo 'id', y debe generarse automaticamente, así tambien se agregó el campo 'name' tipo String.

```
    @Entity
    @Table(name = "category")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public class Category {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String name;
    }
```

El segunda entidad es Products la cual debe llevar el foreign key de Category, por lo cual se hace uso de la anotación @ManyToOne en el campo 'category' lo cual quiere decir que una categoría posee muchos productos.

```
    @Entity
    @Table(name = "product")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String name;
        private String url_image;
        private float price;
        private int discount;

        @ManyToOne(fetch =FetchType.EAGER)
        @JoinColumn(name="category", nullable=true, referencedColumnName = "id")
        private Category category;
    }
```

## Repositorios

Para el acceso a la base de datos se generó dos repositorios. El primero es CategoryRepository, es una interfaz que extiende los metodos de la interfaz JpaRepository, ésta última tiene por defecto métodos como findAll() que permite obtener todas los registros.

```
    public interface CategoryRepository extends JpaRepository<Category, Integer> {
    }

    @NoRepositoryBean
    public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
        List<T> findAll();

        List<T> findAll(Sort sort);

        List<T> findAllById(Iterable<ID> ids);

        <S extends T> List<S> saveAll(Iterable<S> entities);

        void flush();
        ...
```

El repositorio de Products es ProductsRepository el cual a diferencia de CategoryRepository extiende la interfaz PagingAndSortingRepository que permite interactuar con la tabla de Products pero de forma paginada. Aquí se realizó la lógica que cada una los requests, teniendo un total de tres queries.

```
    public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
        //Query para obtener productos por categoria previniendo sql inyection con parametro indexado y con paginacion
        @Query(value = "select id,name,url_image,price,discount,category from product where category = ?1",
                countQuery = "select count(*) from product where category = ?1", nativeQuery = true)
        Page<Product> findByCategory(@Param("category") Integer category, Pageable pageable);

        //Query para obtener productos por busqueda de texto previniendo sql inyection con parametro indexado y con paginacion
        @Query(value = "select id,name,url_image,price,discount,category from product where name like CONCAT('%', ?1, '%')",
                countQuery = "select count(*) from product where name like '%?1%'", nativeQuery = true)
        Page<Product> findByText(@Param("text") String text, Pageable pageable);

        //Query para obtener productos por rango de precios previniendo sql inyection con parametro indexado y con paginacion
        @Query(value = "select id,name,url_image,price,discount,category from product where price between ?1 and ?2",
                countQuery = "select count(*) from product where price between ?1 and ?2", nativeQuery = true)
        Page<Product> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);
    }
```

## Servicios

El primer servicio, CategoryService, tiene un solo método que permite obtener todas las categorias de la base de datos.

```
@Service
    public class CategoryService {
        //Inyeccion de dependencia repositorio
        @Autowired
        CategoryRepository categoryRepository;

        //Obtener todas las categorias
        public List<Category> findAll() {
            return categoryRepository.findAll();
        }

    }
```

El segundo servicio, ProductService, posee cuatro métodos los cuales permiten obtener todos los productos,
productos por categoría, productos por filtro de texto y productos por rango de precios, cada uno paginados.

```
    @Service
    public class ProductService {
        //Inyeccion de dependencia repositorio
        @Autowired
        ProductRepository productRepository;

        //Obtener todas los productos
        public Page<Product> findAll(Pageable pageable) {
            return productRepository.findAll(pageable);
        }

        //Obtener todos los productos por categoria
        public Page<Product> findAllByCategory(Integer category, Pageable pageable) {
            return productRepository.findByCategory(category, pageable);
        }

        //Obtener todos los productos por texto busqueda
            public Page<Product> findAllByText(String text, Pageable pageable) {
            return productRepository.findByText(text, pageable);
        }

        //Obtener todos los productos por rango de precios
        public Page<Product> findAllByPriceRange(Integer minPrice, Integer maxPrice, Pageable pageable) {
            return productRepository.findByPriceRange(minPrice,maxPrice, pageable);
        }
    }
```

## Rest Controllers

CategoryController brinda el endpoint '/categories' tipo get para obtener todos las categorias permitiendo el acceso cross origin a todos los origenes.

```
    @RestController
    @RequestMapping(value = "categories")
    public class CategoryController {
        //Inyeccion de dependencia service
        @Autowired
        private CategoryService categoryService;

        //Allow CrossOrigin a todos los clientes
        @CrossOrigin(origins = "*")
        //Petición Get con ruta base /categories
        @GetMapping
        public ResponseEntity<List<Category>> getAllCategories() {
            //Obtener todos las categorias desde la base de datos
            List<Category> categories = categoryService.findAll();

            //Retornar un ResponseEnitity con las categorias y el HttpStatus 200 al cliente
            return new ResponseEntity<List<Category>>(categories,null, HttpStatus.OK);
        }
    }
```

El ProductController brinda el endpoint 'products' que acepta parametro metros para poder obtener los productos filtrados, se hace uso de una sola función en la cual se valida que parametro está siendo enviado por el cliente para a partir de eso decidir que servicio de products utlizar y devolver una respuesta pagina al cliente.

```
    @RestController
    //Direccion base de las peticiones
    @RequestMapping(value = "/products")
    public class ProductController {
        //Inyeccion dependencia service
        @Autowired
        private ProductService productService;

        //Allow CrossOrigin a todos los clientes
        @CrossOrigin(origins = "*")
        //Petición Get con ruta base /products con paginacion de 8 elementos por pagina
        @GetMapping
        public ResponseEntity<?> getAllProductsByFilter(@Nullable @RequestParam("category") Integer category,
                                                        @Nullable @RequestParam("text") String text,
                                                        @Nullable @RequestParam("minPrice") Integer minPrice,
                                                        @Nullable @RequestParam("maxPrice") Integer maxPrice,
                                                                    @RequestParam(defaultValue = "0") Integer pageNo,
                                                                    @RequestParam(defaultValue = "8") Integer pageSize,
                                                                    @RequestParam(defaultValue = "id") String sortBy) {
            List<Product> products = new ArrayList<>();
            Page<Product> pagedResult;
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
            //Validar que se envíe el parametro category o text o min price
            if (category != null) {
                //Obtener todos los productos por categoria desde la base de datos
                pagedResult = productService.findAllByCategory(category,paging);
            } else if (text != null){
                //Obtener todos los productos filtrado por texto
                pagedResult =  productService.findAllByText(text,paging);
            } else if (minPrice != null) {
                //Obtener todos los productos filtrado por rango de precios
                pagedResult =  productService.findAllByPriceRange(minPrice,maxPrice,paging);
            } else {
                //Obtener todos los productos desde la base de datos
                pagedResult =  productService.findAll(paging);
            }

            //Si pagedReult contiene datos almacenarlo en array list products
            if(pagedResult.hasContent()) {
                products = pagedResult.getContent();
            }

            //Formar un map que contengo los productos y datos de paginacion
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("currentPage", pagedResult.getNumber());
            response.put("totalItems", pagedResult.getTotalElements());
            response.put("totalPages", pagedResult.getTotalPages());

            //Retornar un ResponseEnitity con los datos de response y el HttpStatus 200 al cliente
            return new ResponseEntity<>(response,null, HttpStatus.OK);
        }

    }
```

# Keep Alive Strategy

## Uso de Scheduler de Spring boot

Para poder mantener la conexión a la base de datos y no perderla por inactivadad se hizo uso de Scheduled
con lo cual se hizo una tarea programada que obtenga datos de la base de datos cada 4.25 segundos, se hizo uso del serivicio CategoryService ya que la cantidad de registros de categorias es mínimo y la cantidad de campos también, de tal forma que la obtención de datos se más ligera y no afecte al rendimiento.

```
  @Component
  public class RequestScheduler {
      /*Inyectar dependencia del service de categorias*/
      @Autowired
      private CategoryService categoryService;

      /*Tarea programada para ejecutar una peticion a la base de datos cada 4250 milisegundos*/
      @Async
      @Scheduled(fixedRate = 4250)
      public List<Category> requestDBCategory() {
          //Devolver todas las categorias
          return categoryService.findAll();
      }
  }
```

# Prevenir Sql Inyection

## Uso de parámetros indexados en las consultas a la base de datos

Para prevenir los ataques de Sql Inyection se hizo uso de parámetros indexados en el repositorio de ProductRepository, de esta forma se mantiene a la api más segura.

```
    //Query para obtener productos por categoria previniendo sql inyection con parametro indexado y con paginacion
    @Query(value = "select id,name,url_image,price,discount,category from product where category = ?1",
            countQuery = "select count(*) from product where category = ?1", nativeQuery = true)
    Page<Product> findByCategory(@Param("category") Integer category, Pageable pageable);
```

# Deployment

## AWS Instance

Para el despliegue de la aplicación se usó amazon web services con una cuenta gratuita, donde se lanzó una instancia EC2 con el sistema operativo Ubuntu, se activo los puertos http, https, para todos los origenes de forma que la api se accesibles. Así también se configuró una ip elástica para la instancia, de tal modo que al reiniciar la instancia no se modifique la ipv4 pública.

## Configuración de entorno virtual Ubuntu

Se ingresó a la máquina virtual a través de putty con el uso de una llave par de claves .PPK, se actualizó el sistema operativo y se instaló java 11.

```
    sudo apt-get update 
    sudo apt-get install openjdk-11-jdk
```

## Uso de Systemd Service para deploy de app spring boot

Se utilizó el método de systemd Service para correr la aplicación con un archivo .service.
El archivo se generó en la ruta "etc/systemd/system/" con el nombre "springstart.service", el archivo contiene las instrucciones para correr la aplicación spring boot en el puerto 8080.

```
    /*service deploy*/
    sudo nano /etc/systemd/system/springstart.service

    [Unit]
    Description=Api Tienda BSale
    After=syslog.target

    [Service]
    User=ubuntu
    ExecStart=/usr/bin/java -jar -Dspring.profiles.active=aws -Dserver.port=8080 /home/ubuntu/app.jar
    SuccessExitStatus=143

    [Install]
    WantedBy=multi-user.target
```

## Activación y Monitoreo de logs, y la aplicación

Para activar y dar seguimiento a los logs de la aplicación o al status del servicio se tiene los siguientes comandos.

```
    sudo systemctl enable springstart.service
    sudo systemctl status springstart
    sudo systemctl start springstart
    sudo journalctl -f -u springstart
```

# Uso de la API

## EndPoint /products

Este endpoint devuelve la siguiente estructura JSON; "totalItems" indica el total de productos que existen con el filtro, "totalPages" la cantidad de paginas de ocho productos cada una, "currentPage" la pagina actual a la que se hizo el request y "products" que muestra un array de productos como resultado de la consulta. Si no se le envía ningún parametro al endpoint se devolverán todas los productos.

```
    {
        "totalItems": ,
        "totalPages": 8,
        "currentPage": 0,
        "products": [
            {
                "id": 5,
                "name": "ENERGETICA MR BIG",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/misterbig3308256.jpg",
                "price": 1490.0,
                "discount": 20,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            }
        ]
    }
```

### Parametro /prodcuts?category

Este parametro permite filtrar los productos por categoría. De igual forma si se agrega el parámetro adicional pageNo, que empieza en 0 siendo 0 la primera página, se devolverá la pagina solicitada.

Ejemplo:

API
```
    http://34.205.223.61:8080/products?category=2&pageNo=2
```

RESPONSE
```
    {
        "totalItems": 21,
        "totalPages": 3,
        "currentPage": 2,
        "products": [
            {
                "id": 88,
                "name": "PISCO MISTRAL GRAN NOBEL 40°",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/grannobel9104.jpg",
                "price": 19900.0,
                "discount": 0,
                "category": {
                    "id": 2,
                    "name": "pisco"
                }
            },
            {
                "id": 89,
                "name": "PISCO MISTRAL 40°",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/409346.jpg",
                "price": 4990.0,
                "discount": 0,
                "category": {
                    "id": 2,
                    "name": "pisco"
                }
            },
            {
                "id": 90,
                "name": "PISCO MISTRAL 46°",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/469463.jpg",
                "price": 7890.0,
                "discount": 0,
                "category": {
                    "id": 2,
                    "name": "pisco"
                }
            },
            {
                "id": 91,
                "name": "PISCO MISTRAL NOBEL 40°",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/nobel409551.jpg",
                "price": 19990.0,
                "discount": 0,
                "category": {
                    "id": 2,
                    "name": "pisco"
                }
            },
            {
                "id": 92,
                "name": "PISCO MISTRAL NOBEL 46",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/nobelanejado9639.jpg",
                "price": 15990.0,
                "discount": 15,
                "category": {
                    "id": 2,
                    "name": "pisco"
                }
            }
        ]
    }
```

### Parametro /prodcuts?text

Este parametro permite filtrar los productos por texto de búsqueda. De igual forma si se agrega el parámetro adicional pageNo, que empieza en 0 siendo 0 la primera página, se devolverá la pagina solicitada.

Ejemplo:

API
```
    http://34.205.223.61:8080/products?text=bebida&pageNo=0
```

RESPONSE
```
    {
    "totalItems": 1,
    "totalPages": 1,
    "currentPage": 0,
    "products": [
        {
            "id": 68,
            "name": "Bebida Sprite 1 Lt",
            "url_image": null,
            "price": 1250.0,
            "discount": 10,
            "category": {
                "id": 4,
                "name": "bebida"
            }
        }
    ]
}
```

### Parametro /prodcuts?minPrice=&maxPrice=

Estos parametros permiten filtrar los productos por rango de precios. De igual forma si se agrega el parámetro adicional pageNo, que empieza en 0 siendo 0 la primera página, se devolverá la pagina solicitada.

Ejemplo:

API
```
    http://34.205.223.61:8080/products?minPrice=1000&maxPrice=2000&pageNo=0
```

RESPONSE
```
    {
        "totalItems": 17,
        "totalPages": 3,
        "currentPage": 0,
        "products": [
            {
                "id": 5,
                "name": "ENERGETICA MR BIG",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/misterbig3308256.jpg",
                "price": 1490.0,
                "discount": 20,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            },
            {
                "id": 6,
                "name": "ENERGETICA RED BULL",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/redbull8381.jpg",
                "price": 1490.0,
                "discount": 0,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            },
            {
                "id": 7,
                "name": "ENERGETICA SCORE",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/logo7698.png",
                "price": 1290.0,
                "discount": 0,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            },
            {
                "id": 34,
                "name": "ENERGETICA MONSTER RIPPER",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/mosterriper0436.jpg",
                "price": 1990.0,
                "discount": 0,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            },
            {
                "id": 35,
                "name": "ENERGETICA MAKKA DRINKS",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/makka-drinks-250ml0455.jpg",
                "price": 1190.0,
                "discount": 0,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            },
            {
                "id": 36,
                "name": "ENERGETICA MONSTER VERDE",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/monsterverde0476.jpg",
                "price": 1990.0,
                "discount": 0,
                "category": {
                    "id": 1,
                    "name": "bebida energetica"
                }
            },
            {
                "id": 37,
                "name": "COCA COLA ZERO DESECHABLE",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/cocazero9766.jpg",
                "price": 1490.0,
                "discount": 0,
                "category": {
                    "id": 4,
                    "name": "bebida"
                }
            },
            {
                "id": 48,
                "name": "SPRITE 1 1/2 Lts",
                "url_image": "https://dojiw2m9tvv09.cloudfront.net/11132/product/sprite-lata-33cl5575.jpg",
                "price": 1500.0,
                "discount": 0,
                "category": {
                    "id": 4,
                    "name": "bebida"
                }
            }
        ]
    }
```

## EndPoint /categories

Este endpoint devuelve todas las categorias con la siguiente estructura JSON; "id" indica el identificador de la categoría y "name" el nombre de la categoría. Este endpoint no admite parámetros, ya que sólo envía todos las categorías que existen en la base de datos.

RESPONSE
```
    [
        {
            "id": 1,
            "name": "bebida energetica"
        },
        {
            "id": 2,
            "name": "pisco"
        },
        {
            "id": 3,
            "name": "ron"
        },
        {
            "id": 4,
            "name": "bebida"
        },
        {
            "id": 5,
            "name": "snack"
        },
        {
            "id": 6,
            "name": "cerveza"
        },
        {
            "id": 7,
            "name": "vodka"
        }
    ]
```









