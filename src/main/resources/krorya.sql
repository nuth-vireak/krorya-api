-- Create the database
-- CREATE DATABASE krorya_db;

-- Enable uuid-ossp extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create 'users' table if it does not exist
CREATE TABLE IF NOT EXISTS users
(
    user_id          UUID        DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                 -- Unique identifier for each user
    username         VARCHAR(50)                            NOT NULL,                             -- Username (up to 50 characters)
    email            VARCHAR(100)                           NOT NULL,                             -- Email (up to 100 characters)
    password         VARCHAR(80)                            NOT NULL,                             -- Encrypted password (up to 80 characters)
    profile_image    VARCHAR(255)                           NOT NULL,                             -- URL or path to profile image
    bio              VARCHAR(120),                                                                -- Short bio or description (optional)
    is_deactivated   BOOLEAN     DEFAULT FALSE,                                                   -- Whether the account is deactivated
    role             VARCHAR(50) DEFAULT 'ROLE_USER' CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER')), -- Role with allowed values 'ROLE_ADMIN' or 'ROLE_USER'
    followings_count INTEGER     DEFAULT 0,                                                       -- Number of users this user follows
    follower_count   INTEGER     DEFAULT 0,                                                       -- Number of users following this user
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP                                        -- Timestamp of account creation
    );

-- Create 'otps' table if it does not exist
CREATE TABLE IF NOT EXISTS otps
(
    otp_id             UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,     -- Unique identifier for the OTP
    otp_code           VARCHAR(6)                           NOT NULL,                 -- The actual OTP code (6 characters)
    issued_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL,                 -- Timestamp of when the OTP was issued
    expired_date       TIMESTAMP                            NOT NULL,                 -- Expiration date of the OTP
    is_verified        BOOLEAN   DEFAULT FALSE              NOT NULL,                 -- Indicates if the OTP has been verified
    user_id            UUID                                 NOT NULL REFERENCES users -- Foreign key referencing 'users' table
    ON UPDATE CASCADE
    ON DELETE CASCADE,                                                            -- Cascade on update and delete for 'user_id'
    is_verified_forget BOOLEAN                              NOT NULL                  -- Indicates if the OTP is for password recovery
    );

-- Notifications table
CREATE TABLE notifications
(
    notification_id UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the notification
    user_id         UUID                                 NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    title           VARCHAR(100)                         NOT NULL,                                                      -- Title of the notification
    description     VARCHAR(255)                         NOT NULL,                                                      -- Description of the notification
    type            VARCHAR(50)                          NOT NULL,                                                      -- Type of the notification
    date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL                                                       -- Timestamp of when the notification was created
);

-- Ingredients table
CREATE TABLE ingredients
(
    ingredient_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,      -- Unique identifier for the ingredient
    user_id       UUID REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    icon          VARCHAR(255)                    NOT NULL,                  -- URL or path to the ingredient icon
    name          VARCHAR(100)                    NOT NULL,                  -- Name of the ingredient
    type          VARCHAR(100)                    NOT NULL                   -- Type of the ingredient
);

-- Cuisines table
CREATE TABLE cuisines
(
    cuisine_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY, -- Unique identifier for the cuisine
    name       VARCHAR(100)                    NOT NULL              -- Name of the cuisine
);

-- Recipes table
CREATE TABLE recipes
(
    recipe_id      UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                             -- Unique identifier for the recipe
    cuisine_id     UUID                                 NOT NULL REFERENCES cuisines ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'cuisines' table
    image          VARCHAR(255),                                                                                          -- URL or path to the recipe image (optional)
    description    VARCHAR(255),                                                                                          -- Description of the recipe (optional)
    cooking_level  VARCHAR(100),                                                                                          -- Cooking level of the recipe (e.g., easy, medium, hard)
    cooking_time   INTEGER,                                                                                               -- Cooking time in minutes (optional)
    serving_number INTEGER,                                                                                               -- Number of servings (optional)
    title          VARCHAR(100),                                                                                          -- Title of the recipe (up to 100 characters)
    is_draft       BOOLEAN   DEFAULT FALSE,                                                                               -- Whether the recipe is a draft or not
    is_public      BOOLEAN   DEFAULT FALSE,                                                                               -- Whether the recipe is public or not
    is_bookmarked  BOOLEAN   DEFAULT FALSE,                                                                               -- Whether the recipe is bookmarked or not
    creator        UUID                                 NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE,    -- Foreign key referencing 'users' table
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL                                                          -- Timestamp of when the recipe was created
);

-- Cooking steps table
CREATE TABLE cooking_steps
(
    cooking_step_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                            -- Unique identifier for the cooking step
    recipe_id       UUID                            NOT NULL REFERENCES recipes ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'recipes' table
    step_number     INTEGER                         NOT NULL,                                                        -- Step number of the cooking step
    image           VARCHAR(255),                                                                                    -- URL or path to the cooking step image (optional)
    description     VARCHAR(255)                    NOT NULL                                                         -- Description of the cooking step
);

-- Tags table
CREATE TABLE tags
(
    tag_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY, -- Unique identifier for the tag
    icon   VARCHAR(100)                    NOT NULL,             -- URL or path to the tag icon
    name   VARCHAR(150)                    NOT NULL,             -- Name of the tag
    type   VARCHAR(50)                     NOT NULL              -- Type of the tag
);

-- Grocery lists table
CREATE TABLE grocery_lists
(
    grocery_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the grocery list
    user_id    UUID                            NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    title      VARCHAR(100)                    NOT NULL                                                       -- Title of the grocery list
);

-- Addresses table
CREATE TABLE addresses
(
    address_id      UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the address
    buyer_id        UUID                            NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    phone_number    VARCHAR(20)                     NOT NULL,                                                      -- Phone number of the buyer
    buyer_latitude  VARCHAR(100)                    NOT NULL,                                                      -- Latitude of the buyer
    buyer_longitude VARCHAR(100)                    NOT NULL                                                       -- Longitude of the buyer
);

-- Orders table
CREATE TABLE orders
(
    order_id     UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                              -- Unique identifier for the order
    buyer_id     UUID                                 NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE,     -- Foreign key referencing 'users' table
    address_id   UUID                                 NOT NULL REFERENCES addresses ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'addresses' table
    description  VARCHAR(255)                         NOT NULL,                                                          -- Description of the order
    total_amount NUMERIC(10, 2)                       NOT NULL,                                                          -- Total amount of the order
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                                                    -- Timestamp of when the order was created
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL                                                           -- Timestamp of when the order was last updated
);

-- Payments table
CREATE TABLE payments
(
    transaction_id UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the transaction
    buyer_id       UUID                                 NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    payment_status VARCHAR(50)                          NOT NULL CHECK (payment_status IN ('unpaid', 'paid')),         -- Payment status with allowed values 'unpaid' or 'paid'
    mode           VARCHAR(50)                          NOT NULL CHECK (mode IN ('cash', 'qrcode')),                   -- Payment mode with allowed values 'cash' or 'qrcode'
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL,                                                      -- Timestamp of when the payment was created
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL                                                       -- Timestamp of when the payment was last updated
);

-- Foods table
CREATE TABLE foods
(
    food_id       UUID             DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the food
    seller_id     UUID                                        NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    category_id   UUID                                        NOT NULL,                                                      -- Foreign key referencing 'categories' table
    food_name     VARCHAR(100)                                NOT NULL,                                                      -- Name of the food
    description   VARCHAR(255)                                NOT NULL,                                                      -- Description of the food
    price         NUMERIC(10, 2)                              NOT NULL,                                                      -- Price of the food
    star_average  DOUBLE PRECISION DEFAULT 0,                                                                                -- Average rating of the food (0-5)
    total_rater   INTEGER          DEFAULT 0,                                                                                -- Total number of raters for the food
    image         VARCHAR(255)                                NOT NULL,                                                      -- URL or path to the food image
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  NOT NULL,                                                      -- Timestamp of when the food was created
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  NOT NULL,                                                      -- Timestamp of when the food was last updated
    is_bookmarked BOOLEAN          DEFAULT FALSE              NOT NULL                                                       -- Whether the food is bookmarked or not
);

-- Categories table
CREATE TABLE categories
(
    category_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY, -- Unique identifier for the category
    name        VARCHAR(30)                     NOT NULL,             -- Name of the category
    icon        VARCHAR(255)                    NOT NULL              -- URL or path to the category icon
);

-- Carts table
CREATE TABLE carts
(
    cart_id    UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                           -- Unique identifier for the cart
    order_id   UUID                                 NOT NULL REFERENCES orders ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'orders' table
    food_id    UUID                                 NOT NULL REFERENCES foods ON UPDATE CASCADE ON DELETE CASCADE,  -- Foreign key referencing 'foods' table
    quantity   INTEGER                              NOT NULL,                                                       -- Quantity of the food
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                                                 -- Timestamp of when the cart was created
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL                                                        -- Timestamp of when the cart was last updated
);

-- Tokens table
CREATE TABLE tokens
(
    tokenid            UUID                     DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                        -- Unique identifier for the token
    userid             UUID                                                NOT NULL REFERENCES users ON DELETE CASCADE, -- Foreign key referencing 'users' table
    accesstoken        VARCHAR(512)                                        NOT NULL,                                    -- Access token
    refreshtoken       VARCHAR(512)                                        NOT NULL,                                    -- Refresh token
    accesstokenexpiry  TIMESTAMP WITH TIME ZONE                            NOT NULL,                                    -- Access token expiry
    refreshtokenexpiry TIMESTAMP WITH TIME ZONE                            NOT NULL,                                    -- Refresh token expiry
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,                                              -- Timestamp of when the token was created
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP                                               -- Timestamp of when the token was last updated
);

-- Follows table
CREATE TABLE follows
(
    follow_id    UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the follow
    following_id UUID                            NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    follower_id  UUID                            NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    UNIQUE (follower_id, following_id)
);

-- Food bookmarks table
CREATE TABLE food_bookmark
(
    user_id UUID NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    food_id UUID NOT NULL REFERENCES foods ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'foods' table
    PRIMARY KEY (user_id, food_id)                                              -- Composite primary key of 'user_id' and 'food_id'
);

-- Recipe bookmarks table
CREATE TABLE recipe_bookmark
(
    user_id   UUID NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE,   -- Foreign key referencing 'users' table
    recipe_id UUID NOT NULL REFERENCES recipes ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'recipes' table
    PRIMARY KEY (user_id, recipe_id)                                                -- Composite primary key of 'user_id' and 'recipe_id'
);

-- Feedbacks table
CREATE TABLE feedbacks
(
    feedback_id   UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,      -- Unique identifier for the feedback
    parent_id     UUID REFERENCES feedbacks,                                      -- Foreign key referencing 'feedbacks' table
    food_id       UUID                                 NOT NULL REFERENCES foods, -- Foreign key referencing 'foods' table
    commentator   UUID                                 NOT NULL REFERENCES users, -- Foreign key referencing 'users' table
    comment       VARCHAR                              NOT NULL,                  -- Comment of the feedback
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             -- Timestamp of when the feedback was created
);

-- Reports table
CREATE TABLE reports
(
    report_id   UUID      DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the report
    recipe_id   UUID REFERENCES recipes ON UPDATE CASCADE ON DELETE CASCADE,                                        -- Foreign key referencing 'recipes' table
    reporter    UUID                                 NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    user_id     UUID REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE,                                          -- Foreign key referencing 'users' table
    description VARCHAR(255)                         NOT NULL,                                                      -- Description of the report
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                                                -- Timestamp of when the report was created
    type        VARCHAR(50) CHECK (type IN ('recipe', 'user'))                                                      -- Type of the report with allowed values 'recipe' or 'user'
);

-- Rates table
CREATE TABLE rates
(
    rate_id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,                                          -- Unique identifier for the rate
    food_id UUID                            NOT NULL REFERENCES foods ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'foods' table
    rater   UUID                            NOT NULL REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'users' table
    star    INTEGER                         NOT NULL                                                       -- Star rating (0-5) of the food
);

-- Recipe tags table
CREATE TABLE recipe_tag
(
    recipe_id UUID NOT NULL REFERENCES recipes ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'recipes' table
    tag_id    UUID NOT NULL REFERENCES tags ON UPDATE CASCADE ON DELETE CASCADE     -- Foreign key referencing 'tags' table
);

-- Recipe ingredients table
CREATE TABLE recipe_ingredient
(
    recipe_id     UUID         NOT NULL REFERENCES recipes ON UPDATE CASCADE ON DELETE CASCADE,     -- Foreign key referencing 'recipes' table
    ingredient_id UUID         NOT NULL REFERENCES ingredients ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'ingredients' table
    qty           VARCHAR(100) NOT NULL DEFAULT 'string',                                           -- Quantity of the ingredient
    PRIMARY KEY (recipe_id, ingredient_id)                                                          -- Composite primary key of 'recipe_id' and 'ingredient_id'
);

-- Grocery recipe table
CREATE TABLE grocery_recipe
(
    grocery_id UUID NOT NULL REFERENCES grocery_lists ON UPDATE CASCADE ON DELETE CASCADE, -- Foreign key referencing 'grocery_lists' table
    recipe_id  UUID NOT NULL REFERENCES recipes ON UPDATE CASCADE ON DELETE CASCADE,       -- Foreign key referencing 'recipes' table
    PRIMARY KEY (grocery_id, recipe_id)                                                    -- Composite primary key of 'grocery_id' and 'recipe_id'
);

-- Create indexes
CREATE INDEX idx_cuisine_id ON recipes (cuisine_id);        -- Index for 'cuisine_id' column in 'recipes' table
CREATE INDEX idx_cooking_level ON recipes (cooking_level);  -- Index for 'cooking_level' column in 'recipes' table
CREATE INDEX idx_title ON recipes (title);                  -- Index for 'title' column in 'recipes' table
CREATE INDEX idx_is_draft ON recipes (is_draft);            -- Index for 'is_draft' column in 'recipes' table

CREATE INDEX idx_food_name ON foods (food_name);            -- Index for 'food_name' column in 'foods' table
CREATE INDEX idx_seller_id ON foods (seller_id);            -- Index for 'seller_id' column in 'foods' table
CREATE INDEX idx_price ON foods (price);                    -- Index for 'price' column in 'foods' table

CREATE INDEX idx_following_id ON follows (following_id);    -- Index for 'following_id' column in 'follows' table
CREATE INDEX idx_follower_id ON follows (follower_id);      -- Index for 'follower_id' column in 'follows' table

CREATE INDEX idx_parent_id ON feedbacks (parent_id);        -- Index for 'parent_id' column in 'feedbacks' table

-- Follow counts triggers
CREATE OR REPLACE FUNCTION increment_follow_counts() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
UPDATE users SET followings_count = followings_count + 1 WHERE user_id = NEW.follower_id;
UPDATE users SET follower_count = follower_count + 1 WHERE user_id = NEW.following_id;
RETURN NEW;
END;
$$;

CREATE TRIGGER trigger_increment_follow_counts
    AFTER INSERT
    ON follows
    FOR EACH ROW
    EXECUTE FUNCTION increment_follow_counts();

CREATE OR REPLACE FUNCTION decrement_follow_counts() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
UPDATE users SET followings_count = followings_count - 1 WHERE user_id = OLD.follower_id;
UPDATE users SET follower_count = follower_count - 1 WHERE user_id = OLD.following_id;
RETURN OLD;
END;
$$;

CREATE TRIGGER trigger_decrement_follow_counts
    AFTER DELETE
    ON follows
    FOR EACH ROW
    EXECUTE FUNCTION decrement_follow_counts();

-- Update food rating stats triggers
CREATE OR REPLACE FUNCTION update_food_rating_stats() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
UPDATE foods SET total_rater = total_rater + 1 WHERE food_id = NEW.food_id;
UPDATE foods
SET star_average = (SELECT SUM(star)::FLOAT / COUNT(*) FROM rates WHERE food_id = NEW.food_id)
WHERE food_id = NEW.food_id;
RETURN NEW;
END;
$$;

CREATE TRIGGER trigger_update_food_rating_stats
    AFTER INSERT
    ON rates
    FOR EACH ROW
    EXECUTE FUNCTION update_food_rating_stats();

-- Insert initial data into cuisines
INSERT INTO cuisines (name)
VALUES ('Banteay Meanchey'),
       ('Battambang'),
       ('Kampong Cham'),
       ('Kampong Chhnang'),
       ('Kampong Speu'),
       ('Kampong Thom'),
       ('Kampot'),
       ('Kandal'),
       ('Kep'),
       ('Koh Kong'),
       ('Kratié'),
       ('Mondulkiri'),
       ('Oddar Meanchey'),
       ('Pailin'),
       ('Phnom Penh'),
       ('Preah Sihanouk'),
       ('Preah Vihear'),
       ('Pursat'),
       ('Prey Veng'),
       ('Ratanakiri'),
       ('Siem Reap'),
       ('Stung Treng'),
       ('Svay Rieng'),
       ('Takéo'),
       ('Tbong Khmum');