import sqlite3 as sq
import pandas as pd

def first_task(con):
    SELECT_SQL = """
            SELECT 
                r.room_name AS 'Номер',
                tr.type_room_name AS 'Тип номера',
                tr.price  AS 'Цена'
            FROM room r 
            NATURAL JOIN type_room tr 
            WHERE r.room_name LIKE '%-09%' OR r.room_name  LIKE '%-10%'"""
    cursor = con.cursor()
    cursor.execute(SELECT_SQL)
    for result in cursor.fetchall():
        print(result)

def second_task(con):
    SELECT_SQL = """
        SELECT 
            s.service_name AS Услуга,
            IFNULL(COUNT(sb.service_id), '-') AS Количество,
            IFNULL(SUM(sb.price), 0)  AS Сумма
        FROM service s 
        NATURAL LEFT JOIN service_booking sb
        GROUP BY s.service_id
        ORDER BY Сумма DESC, Количество DESC, Услуга"""
    df = pd.read_sql(SELECT_SQL, con)
    print(df)

def third_task(con):
    SELECT_SQL = """
        SELECT 
            g.guest_name AS ФИО_гостя,
            r.room_name AS Номер,
            JULIANDAY(rb.check_out_date) - JULIANDAY(rb.check_in_date)  AS Количество_дней,
            tr.type_room_name AS Тип_номера
        FROM room_booking rb 
        NATURAL LEFT JOIN guest g
        NATURAL JOIN room r 
        LEFT JOIN type_room tr ON tr.type_room_id = r.type_room_id 
        WHERE Количество_дней = (SELECT MAX(JULIANDAY(rb1.check_out_date) - JULIANDAY(rb1.check_in_date)) FROM room_booking rb1)
        ORDER BY ФИО_гостя"""
    df = pd.read_sql(SELECT_SQL, con)
    print(df)

def fourth_task(con):
    DROP_BILL_TABLE_SQL = "DROP TABLE IF EXISTS bill"
    CREATE_BILL_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS bill(
            bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
            guest_check_in_out VARCHAR(510) NOT NULL,
            service_dates VARCHAR(510) NOT NULL,
            deposite_difference REAL NOT NULL
        )"""
    FILL_BILL_TABLE_SQL = """
        INSERT INTO bill(guest_check_in_out, service_dates, deposite_difference)
        SELECT 
            g.guest_name || ' ' || rb.check_in_date || '/' || rb.check_out_date AS guest_check_in_out,
            s.service_name || ' ' || GROUP_CONCAT(STRFTIME('%Y-%m-%d', sb.service_start_date), ', ') AS service_dates,
            15000 - SUM(sb.price) AS deposite_difference
        FROM room_booking rb 
        NATURAL JOIN guest g 
        NATURAL JOIN room r 
        NATURAL JOIN service_booking sb 
        LEFT JOIN service s ON sb.service_id = s.service_id 
        WHERE g.guest_name = 'Астахов И.И.'
        AND r.room_name = 'С-0206'
        GROUP BY guest_check_in_out, s.service_name"""
    SELECT_SQL = """
        WITH bill_result 
        AS (
            SELECT 
                b.guest_check_in_out,
                b.service_dates,
                SUM(b.deposite_difference) OVER (ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)
                -
                15000 * (ROW_NUMBER() OVER () - 1) AS deposite_remaning
            FROM bill b
        )
        SELECT 
            br.service_dates,
            CASE 
                WHEN br.deposite_remaning > 0 THEN 'Вернуть: ' || br.deposite_remaning
                WHEN br.deposite_remaning = 0 THEN 'Итого: ' || br.deposite_remaning
                ELSE 'Доплатить: ' || br.deposite_remaning
            END AS reposite_result
        FROM bill_result br"""
    cursor = con.cursor()
    cursor.execute(DROP_BILL_TABLE_SQL)
    cursor.execute(CREATE_BILL_TABLE_SQL)
    cursor.execute(FILL_BILL_TABLE_SQL)
    df = pd.read_sql(SELECT_SQL, con)
    print(df)
    cursor.close()

def fifth_task(con):
    WITH_WINDOW_FUNCTION_SQL = """
        WITH a AS(
            SELECT DISTINCT 
                sb.service_id,
                STRFTIME ('%Y-%m', sb.service_start_date) as year_month,
                COUNT(sb.service_id)
                OVER (
                    PARTITION BY 
                        STRFTIME ('%Y-%m', sb.service_start_date),
                        sb.service_id 
                    ORDER BY sb.service_id 
                ) AS count_month,
                AVG(sb.price)
                OVER (
                    PARTITION BY 
                        STRFTIME ('%Y-%m', sb.service_start_date),
                        sb.service_id 
                ) AS avg_month
            FROM service_booking sb 
            ORDER BY year_month, count_month DESC, avg_month DESC
        )
        SELECT
            SUBSTRING(a.year_month,1,4)  AS Год,
            SUBSTRING(a.year_month, 6,2)  AS Месяц,
            s.service_name AS Услуга
        FROM a
        JOIN service s ON a.service_id = s.service_id 
        WHERE a.service_id = (
            SELECT
                sb1.service_id
            FROM service_booking sb1
            WHERE STRFTIME('%Y-%m', sb1.service_start_date) = a.year_month
            GROUP BY sb1.service_id 
            ORDER BY COUNT(sb1.service_id) DESC, AVG(sb1.price) DESC 
            LIMIT 1
        )"""
    WITHOUT_WINDOW_FUNCTION_SQL = """
        WITH unique_year_month(year_month)
        AS (
            SELECT DISTINCT 
                STRFTIME('%Y-%m', sb.service_start_date) 
            FROM service_booking sb 
            WHERE STRFTIME('%Y', sb.service_start_date) IN ('2020', '2021')  
        )
        SELECT 
            SUBSTR(uym.year_month, 1, 4) AS Год,
            SUBSTR(uym.year_month, 6, 7) AS Месяц,
            s.service_name AS Услуга
        FROM unique_year_month uym
        LEFT JOIN service_booking sb ON STRFTIME('%Y-%m', sb.service_start_date) = uym.year_month
        LEFT JOIN service s ON sb.service_id = s.service_id 
        WHERE sb.service_id = (
            SELECT 
                sb.service_id 
            FROM service_booking sb 
            WHERE STRFTIME('%Y-%m', sb.service_start_date) = uym.year_month
            GROUP BY STRFTIME('%Y-%m', sb.service_start_date), sb.service_id 
            ORDER BY COUNT(sb.service_id) DESC, AVG(sb.price) DESC
            LIMIT 1  
        )
        GROUP BY sb.service_id 
        ORDER BY Год, Месяц"""
    df1 = pd.read_sql(WITH_WINDOW_FUNCTION_SQL, con)
    df2 = pd.read_sql(WITHOUT_WINDOW_FUNCTION_SQL, con)
    print(df1)
    print(df2)
    
if __name__ == '__main__':
    with sq.connect('./booking.db') as con:
        try:
            fourth_task(con)
        except e:
            con.rollback()