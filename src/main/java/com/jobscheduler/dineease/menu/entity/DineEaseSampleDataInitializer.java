package com.jobscheduler.dineease.menu.entity;

import com.jobscheduler.dineease.menu.repository.RestaurantRepository;
import com.jobscheduler.dineease.menu.repository.MenuCategoryRepository;
import com.jobscheduler.dineease.menu.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
public class DineEaseSampleDataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurants;
    private final MenuCategoryRepository categories;
    private final MenuItemRepository items;

    public DineEaseSampleDataInitializer(
            RestaurantRepository restaurants,
            MenuCategoryRepository categories,
            MenuItemRepository items) {
        this.restaurants = restaurants;
        this.categories = categories;
        this.items = items;
    }

    record Dish(String category, String name, String description, String price) {}
    record RestaurantData(String name, String description, List<Dish> dishes) {}

    private Dish d(String category, String name, String description, String price) {
        return new Dish(category, name, description, price);
    }

    private List<RestaurantData> data() {
        return List.of(
            new RestaurantData("Pizza Palace", "Freshly baked pizzas and Italian favourites", List.of(
                d("Main Course","Margherita Pizza","Classic tomato, mozzarella and basil","199"),
                d("Starters","Garlic Bread","Toasted garlic bread","99"),
                d("Beverages","Fresh Lime Soda","Refreshing lime soda","69"),
                d("Desserts","Chocolate Brownie","Rich chocolate brownie","119"),
                d("Specials","Farmhouse Pizza","Loaded vegetable pizza","279")
            )),
            new RestaurantData("Burger Hub", "Gourmet burgers, fries and shakes", List.of(
                d("Main Course","Classic Veg Burger","Crispy vegetable patty burger","129"),
                d("Starters","Peri Peri Fries","Seasoned crispy fries","99"),
                d("Beverages","Vanilla Milkshake","Creamy vanilla milkshake","119"),
                d("Desserts","Chocolate Sundae","Chocolate ice cream sundae","139"),
                d("Specials","Double Cheese Burger","Double patty with cheese","219")
            )),
            new RestaurantData("Sushi World", "Japanese-inspired sushi and rice bowls", List.of(
                d("Main Course","Vegetable Sushi Roll","Fresh vegetable sushi roll","249"),
                d("Starters","Edamame Bites","Lightly seasoned edamame","149"),
                d("Beverages","Iced Green Tea","Chilled green tea","89"),
                d("Desserts","Mochi Ice Cream","Sweet ice cream mochi","159"),
                d("Specials","Teriyaki Rice Bowl","Rice bowl with teriyaki vegetables","279")
            )),
            new RestaurantData("Pasta Corner", "Comforting pasta and Italian meals", List.of(
                d("Main Course","Penne Alfredo","Creamy Alfredo penne pasta","229"),
                d("Starters","Bruschetta","Tomato and herb toasted bread","129"),
                d("Beverages","Cold Coffee","Chilled creamy coffee","99"),
                d("Desserts","Tiramisu Cup","Coffee-flavoured layered dessert","169"),
                d("Specials","Pesto Pasta","Pasta tossed in basil pesto","249")
            )),
            new RestaurantData("Taco Fiesta", "Mexican tacos, wraps and bowls", List.of(
                d("Main Course","Paneer Taco","Spiced paneer in a soft taco","159"),
                d("Starters","Nachos","Crispy nachos with salsa","129"),
                d("Beverages","Mango Cooler","Chilled mango drink","89"),
                d("Desserts","Churros","Cinnamon sugar churros","139"),
                d("Specials","Mexican Burrito Bowl","Rice, beans and vegetables","229")
            )),
            new RestaurantData("Biryani House", "Aromatic biryani and Indian favourites", List.of(
                d("Main Course","Veg Dum Biryani","Fragrant rice with vegetables","199"),
                d("Starters","Paneer Tikka","Grilled marinated paneer","179"),
                d("Beverages","Sweet Lassi","Traditional sweet yoghurt drink","79"),
                d("Desserts","Gulab Jamun","Warm syrup-soaked sweets","69"),
                d("Specials","Paneer Biryani","Spiced biryani with paneer","249")
            )),
            new RestaurantData("Dragon Wok", "Asian noodles, rice and wok specials", List.of(
                d("Main Course","Hakka Noodles","Stir-fried vegetable noodles","169"),
                d("Starters","Veg Spring Rolls","Crispy vegetable spring rolls","119"),
                d("Beverages","Lemon Iced Tea","Chilled lemon tea","79"),
                d("Desserts","Chocolate Pancakes","Sweet chocolate pancakes","149"),
                d("Specials","Schezwan Fried Rice","Spicy Schezwan vegetable rice","189")
            )),
            new RestaurantData("Curry Express", "Indian curries and freshly made breads", List.of(
                d("Main Course","Paneer Butter Masala","Paneer in creamy tomato gravy","229"),
                d("Starters","Hara Bhara Kebab","Spinach and vegetable kebabs","149"),
                d("Beverages","Masala Chaas","Spiced buttermilk","59"),
                d("Desserts","Rasmalai","Soft cheese patties in sweet milk","99"),
                d("Specials","Dal Makhani Combo","Slow-cooked lentils with rice","199")
            )),
            new RestaurantData("The Grill", "Grilled meals, sandwiches and sides", List.of(
                d("Main Course","Grilled Veg Sandwich","Toasted vegetable sandwich","139"),
                d("Starters","Grilled Corn","Seasoned grilled corn","89"),
                d("Beverages","Watermelon Juice","Fresh watermelon drink","99"),
                d("Desserts","Caramel Custard","Smooth caramel custard","109"),
                d("Specials","Paneer Grill Platter","Grilled paneer with vegetables","279")
            )),
            new RestaurantData("Sweet Bites", "Desserts, cakes and sweet treats", List.of(
                d("Main Course","Waffle Plate","Freshly made waffle","179"),
                d("Starters","Mini Cupcakes","Assorted mini cupcakes","99"),
                d("Beverages","Strawberry Shake","Creamy strawberry milkshake","129"),
                d("Desserts","Red Velvet Cake","Red velvet cake slice","149"),
                d("Specials","Chocolate Lava Cake","Warm cake with chocolate centre","169")
            ))
        );
    }

    private List<RestaurantData> extraData() {
        return List.of(
            new RestaurantData("Pizza Palace", "", List.of(
                d("Main Course", "Paneer Tikka Pizza", "Pizza topped with paneer tikka", "249"),
                d("Main Course", "Cheese Burst Pizza", "Pizza with a rich cheese-filled crust", "299"),
                d("Main Course", "Mexican Green Wave", "Mexican-style vegetable pizza", "279"),
                d("Starters", "Cheese Garlic Bread", "Toasted garlic bread with cheese", "129"),
                d("Starters", "Stuffed Garlic Bread", "Garlic bread stuffed with cheese and herbs", "159"),
                d("Beverages", "Mango Milkshake", "Creamy mango milkshake", "119"),
                d("Beverages", "Cold Coffee with Ice Cream", "Chilled coffee topped with ice cream", "149"),
                d("Desserts", "Chocolate Mousse", "Rich and creamy chocolate mousse", "129"),
                d("Desserts", "New York Cheesecake", "Classic creamy cheesecake", "179"),
                d("Specials", "Four Cheese Pizza", "Pizza topped with four varieties of cheese", "329")
            )),
            new RestaurantData("Burger Hub", "", List.of(
                d("Main Course", "Crispy Paneer Burger", "Crispy paneer patty with fresh vegetables", "179"),
                d("Main Course", "Mushroom Swiss Burger", "Mushroom burger with Swiss-style cheese", "199"),
                d("Main Course", "Spicy Mexican Burger", "Spicy burger with Mexican-inspired toppings", "189"),
                d("Starters", "Cheese Loaded Fries", "French fries topped with melted cheese", "139"),
                d("Starters", "Potato Wedges", "Seasoned potato wedges", "109"),
                d("Beverages", "Oreo Milkshake", "Chocolate cookie milkshake", "149"),
                d("Beverages", "Mango Smoothie", "Refreshing mango smoothie", "129"),
                d("Desserts", "Brownie with Ice Cream", "Chocolate brownie served with ice cream", "159"),
                d("Desserts", "Chocolate Donut", "Soft donut with chocolate glaze", "89"),
                d("Specials", "Loaded Veg Burger Combo", "Vegetarian burger with fries and a drink", "279")
            )),
            new RestaurantData("Sushi World", "", List.of(
                d("Main Course", "Avocado Sushi Roll", "Sushi roll filled with avocado", "249"),
                d("Main Course", "Cucumber Maki", "Japanese-style cucumber sushi rolls", "199"),
                d("Main Course", "Vegetable Ramen", "Noodles in a flavourful vegetable broth", "229"),
                d("Starters", "Crispy Tofu", "Crispy tofu bites with dipping sauce", "169"),
                d("Starters", "Vegetable Tempura", "Lightly battered and fried vegetables", "189"),
                d("Beverages", "Mango Iced Tea", "Chilled tea with mango flavour", "99"),
                d("Beverages", "Matcha Latte", "Creamy green tea latte", "149"),
                d("Desserts", "Dorayaki", "Japanese-style sweet pancake sandwich", "129"),
                d("Desserts", "Matcha Ice Cream", "Green tea flavoured ice cream", "139"),
                d("Specials", "Tofu Teriyaki Bowl", "Tofu with teriyaki sauce and rice", "269")
            )),
            new RestaurantData("Pasta Corner", "", List.of(
                d("Main Course", "Penne Arrabbiata", "Penne pasta in spicy tomato sauce", "219"),
                d("Main Course", "Mac and Cheese", "Creamy macaroni with cheese sauce", "199"),
                d("Main Course", "Spinach Ricotta Ravioli", "Ravioli filled with spinach and ricotta", "279"),
                d("Starters", "Cheese Croquettes", "Crispy croquettes with cheese filling", "149"),
                d("Starters", "Caprese Salad", "Tomato and cheese salad with herbs", "179"),
                d("Beverages", "Peach Iced Tea", "Chilled peach-flavoured tea", "99"),
                d("Beverages", "Lemon Mint Cooler", "Refreshing lemon and mint drink", "89"),
                d("Desserts", "Chocolate Pudding", "Smooth chocolate pudding", "119"),
                d("Desserts", "Classic Panna Cotta", "Creamy Italian-style dessert", "149"),
                d("Specials", "Baked Lasagna", "Layered pasta with sauce and cheese", "299")
            )),
            new RestaurantData("Taco Fiesta", "", List.of(
                d("Main Course", "Bean and Cheese Burrito", "Burrito filled with beans and cheese", "179"),
                d("Main Course", "Paneer Quesadilla", "Grilled tortilla with paneer and cheese", "199"),
                d("Main Course", "Veg Enchiladas", "Tortillas filled with vegetables and sauce", "219"),
                d("Starters", "Jalapeno Poppers", "Jalapenos filled with creamy cheese", "149"),
                d("Starters", "Loaded Cheese Nachos", "Nachos topped with cheese and vegetables", "169"),
                d("Beverages", "Virgin Mojito", "Mint and lime cooler without alcohol", "119"),
                d("Beverages", "Pineapple Cooler", "Chilled pineapple drink", "109"),
                d("Desserts", "Caramel Churros", "Crispy churros with caramel sauce", "129"),
                d("Desserts", "Chocolate Taco", "Sweet taco filled with chocolate", "139"),
                d("Specials", "Fiesta Veg Platter", "Assorted Mexican-style vegetarian dishes", "329")
            )),
            new RestaurantData("Biryani House", "", List.of(
                d("Main Course", "Hyderabadi Veg Biryani", "Aromatic vegetable biryani with spices", "229"),
                d("Main Course", "Mushroom Biryani", "Flavourful biryani with mushrooms", "219"),
                d("Main Course", "Jeera Rice and Dal Fry", "Cumin rice served with dal fry", "179"),
                d("Starters", "Crispy Corn", "Crispy corn tossed with spices", "139"),
                d("Starters", "Malai Paneer Tikka", "Paneer marinated in a creamy sauce", "219"),
                d("Beverages", "Mango Lassi", "Traditional yoghurt-based mango drink", "99"),
                d("Beverages", "Rose Milk", "Chilled milk flavoured with rose", "89"),
                d("Desserts", "Shahi Tukda", "Bread dessert with creamy topping", "129"),
                d("Desserts", "Kheer", "Traditional rice pudding", "99"),
                d("Specials", "Paneer Biryani Combo", "Paneer biryani with accompaniments", "299")
            )),
            new RestaurantData("Dragon Wok", "", List.of(
                d("Main Course", "Veg Manchurian Fried Rice", "Fried rice with vegetable Manchurian", "219"),
                d("Main Course", "Chilli Garlic Noodles", "Noodles tossed with chilli and garlic", "199"),
                d("Main Course", "Thai Green Curry Rice", "Rice served with Thai-style green curry", "249"),
                d("Starters", "Chilli Paneer", "Paneer tossed in spicy Indo-Chinese sauce", "199"),
                d("Starters", "Crispy Honey Chilli Potato", "Crispy potatoes coated in honey chilli sauce", "169"),
                d("Beverages", "Cold Lemon Tea", "Chilled lemon tea", "89"),
                d("Beverages", "Ginger Lime Cooler", "Refreshing ginger and lime drink", "99"),
                d("Desserts", "Fried Ice Cream", "Ice cream with a crispy outer coating", "159"),
                d("Desserts", "Coconut Pudding", "Smooth coconut-flavoured pudding", "119"),
                d("Specials", "Schezwan Noodle Bowl", "Spicy Schezwan noodles with vegetables", "229")
            )),
            new RestaurantData("Curry Express", "", List.of(
                d("Main Course", "Palak Paneer", "Paneer in a creamy spinach gravy", "219"),
                d("Main Course", "Chole Bhature", "Spiced chickpeas served with bhature", "189"),
                d("Main Course", "Veg Kolhapuri", "Mixed vegetables in spicy Kolhapuri gravy", "209"),
                d("Starters", "Onion Bhaji", "Crispy onion fritters", "99"),
                d("Starters", "Paneer Pakora", "Paneer pieces dipped in spiced batter", "159"),
                d("Beverages", "Mango Chaas", "Refreshing yoghurt drink with mango flavour", "89"),
                d("Beverages", "Jaljeera", "Traditional spiced cumin cooler", "69"),
                d("Desserts", "Moong Dal Halwa", "Traditional sweet lentil dessert", "129"),
                d("Desserts", "Kulfi", "Traditional Indian frozen dessert", "89"),
                d("Specials", "Paneer Thali", "Paneer curry served with rice and breads", "299")
            )),
            new RestaurantData("The Grill", "", List.of(
                d("Main Course", "Paneer Club Sandwich", "Layered sandwich with grilled paneer", "189"),
                d("Main Course", "Grilled Cheese Sandwich", "Toasted sandwich with melted cheese", "149"),
                d("Main Course", "Veggie Wrap", "Wrap filled with grilled vegetables", "159"),
                d("Starters", "Cheese Balls", "Crispy cheese-filled snack balls", "139"),
                d("Starters", "Masala Potato Wedges", "Potato wedges seasoned with Indian spices", "119"),
                d("Beverages", "Orange Juice", "Fresh-style orange juice", "99"),
                d("Beverages", "Cold Cocoa", "Chilled chocolate cocoa drink", "119"),
                d("Desserts", "Apple Pie", "Sweet apple filling in a pastry crust", "139"),
                d("Desserts", "Vanilla Ice Cream", "Classic vanilla ice cream", "89"),
                d("Specials", "Grilled Paneer Sandwich Combo", "Grilled paneer sandwich with fries and a drink", "249")
            )),
            new RestaurantData("Sweet Bites", "", List.of(
                d("Main Course", "Belgian Chocolate Waffle", "Waffle topped with Belgian-style chocolate", "179"),
                d("Main Course", "Blueberry Pancakes", "Fluffy pancakes with blueberry topping", "169"),
                d("Main Course", "Chocolate Crepes", "Thin crepes filled with chocolate", "159"),
                d("Starters", "Chocolate Chip Cookies", "Cookies with chocolate chips", "79"),
                d("Starters", "Cinnamon Rolls", "Soft rolls with cinnamon sugar", "109"),
                d("Beverages", "Hot Chocolate", "Warm creamy chocolate drink", "129"),
                d("Beverages", "Blueberry Smoothie", "Refreshing blueberry smoothie", "139"),
                d("Desserts", "Lotus Biscoff Cheesecake", "Cheesecake with caramelised biscuit flavour", "199"),
                d("Desserts", "Strawberry Pastry", "Light pastry with strawberry cream", "119"),
                d("Specials", "Dessert Celebration Box", "Assorted mini cakes and sweet treats", "349")
            ))
        );
    }
    private List<RestaurantData> combinedData() {
        return java.util.stream.Stream.concat(data().stream(), extraData().stream()).toList();
    }
    @Override
    @Transactional
    public void run(String... args) {
        for (RestaurantData entry : combinedData()) {
            Restaurant restaurant = restaurants.findAll().stream()
                .filter(r -> r.getName().equalsIgnoreCase(entry.name()))
                .findFirst()
                .orElseGet(() -> {
                    Restaurant r = new Restaurant();
                    r.setName(entry.name());
                    r.setDescription(entry.description());
                    r.setAddress("Pune, Maharashtra");
                    r.setActive(true);
                    return restaurants.save(r);
                });

            List<MenuCategory> restaurantCategories =
                categories.findByRestaurant_IdOrderByDisplayOrderAscIdAsc(
                    restaurant.getId());

            for (Dish dish : entry.dishes()) {
                MenuCategory category = restaurantCategories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(dish.category()))
                    .findFirst()
                    .orElseGet(() -> {
                        MenuCategory c = new MenuCategory();
                        c.setName(dish.category());
                        c.setDisplayOrder(restaurantCategories.size() + 1);
                        c.setRestaurant(restaurant);
                        MenuCategory saved = categories.save(c);
                        restaurantCategories.add(saved);
                        return saved;
                    });

                boolean exists = items.findByRestaurantId(restaurant.getId())
                    .stream()
                    .anyMatch(i -> i.getName().equalsIgnoreCase(dish.name()));

                if (!exists) {
                    MenuItem item = new MenuItem();
                    item.setName(dish.name());
                    item.setDescription(dish.description());
                    item.setPrice(new BigDecimal(dish.price()));
                    item.setCategory(category);
                    item.setRestaurant(restaurant);
                    item.setAvailable(true);
                    item.setImageUrl(
                        "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800");
                    items.save(item);
                }
            }
        }

        // Add 24 dishes to DineEase Demo Restaurant; preserve existing dishes.
        Restaurant demo = restaurants.findAll().stream()
            .filter(r -> r.getName().equalsIgnoreCase("DineEase Demo Restaurant"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "DineEase Demo Restaurant was not found"));

        List<Dish> demoDishes = List.of(
            d("Starters", "Vegetable Samosa", "Crispy pastry filled with spiced vegetables", "99"),
            d("Starters", "Hara Bhara Kebab", "Spinach and vegetable kebabs", "149"),
            d("Starters", "Crispy Corn", "Crispy corn tossed with spices", "139"),
            d("Starters", "Chilli Paneer", "Paneer tossed in spicy Indo-Chinese sauce", "199"),
            d("Starters", "Aloo Tikki", "Golden potato patties with Indian spices", "89"),
            d("Main Course", "Masala Dosa", "Crispy dosa with spiced potato filling", "159"),
            d("Main Course", "Chole Bhature", "Spiced chickpeas served with bhature", "189"),
            d("Main Course", "Dal Tadka", "Yellow lentils tempered with Indian spices", "169"),
            d("Main Course", "Shahi Paneer", "Paneer in a rich creamy gravy", "249"),
            d("Main Course", "Veg Manchurian", "Vegetable dumplings in Indo-Chinese sauce", "189"),
            d("Breads and Rice", "Butter Naan", "Soft naan finished with butter", "49"),
            d("Breads and Rice", "Garlic Naan", "Naan topped with garlic and herbs", "69"),
            d("Breads and Rice", "Jeera Rice", "Rice tempered with cumin seeds", "139"),
            d("Breads and Rice", "Veg Pulao", "Fragrant rice cooked with vegetables", "179"),
            d("Breads and Rice", "Tandoori Roti", "Whole-wheat flatbread cooked in a tandoor", "35"),
            d("Desserts", "Gulab Jamun", "Soft sweets soaked in sugar syrup", "89"),
            d("Desserts", "Rasmalai", "Soft cheese patties in sweetened milk", "109"),
            d("Desserts", "Gajar Halwa", "Traditional carrot pudding", "119"),
            d("Desserts", "Kulfi", "Traditional Indian frozen dessert", "89"),
            d("Desserts", "Ice Cream Sundae", "Ice cream with sweet toppings", "129"),
            d("Beverages", "Mango Lassi", "Yoghurt-based mango drink", "99"),
            d("Beverages", "Masala Chai", "Indian tea brewed with aromatic spices", "39"),
            d("Beverages", "Cold Coffee", "Chilled creamy coffee", "99"),
            d("Beverages", "Strawberry Milkshake", "Creamy strawberry milkshake", "129")
        );

        List<MenuCategory> demoCategories =
            categories.findByRestaurant_IdOrderByDisplayOrderAscIdAsc(demo.getId());
        List<MenuItem> existingDemoItems = items.findByRestaurantId(demo.getId());

        for (Dish dish : demoDishes) {
            boolean exists = existingDemoItems.stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase(dish.name()));

            if (exists) {
                continue;
            }

            MenuCategory category = demoCategories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(dish.category()))
                .findFirst()
                .orElseGet(() -> {
                    MenuCategory c = new MenuCategory();
                    c.setName(dish.category());
                    c.setDisplayOrder(demoCategories.size() + 1);
                    c.setRestaurant(demo);
                    MenuCategory saved = categories.save(c);
                    demoCategories.add(saved);
                    return saved;
                });

            MenuItem item = new MenuItem();
            item.setName(dish.name());
            item.setDescription(dish.description());
            item.setPrice(new BigDecimal(dish.price()));
            item.setCategory(category);
            item.setRestaurant(demo);
            item.setAvailable(true);
            item.setImageUrl(
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800");

            items.save(item);
            existingDemoItems.add(item);
        }

        System.out.println(
            "DineEase Demo Restaurant menu additions processed.");
        System.out.println("DineEase sample restaurant and menu data initialized.");
    }
}
