/* 
* TPCW_buy_confirm_servlet.java - Servlet Class implements the buy
*                                 confirm web interaction.
*
* CAVEAT: This servlet does not fully adhere to the TPC-W
*         specification for the buy confirm web interaction, in that
*         it does not communicate with an external payment getway
*         emulator.
*
************************************************************************
*
* This is part of the the Java TPC-W distribution,
* written by Harold Cain, Tim Heil, Milo Martin, Eric Weglarz, and Todd
* Bezenek.  University of Wisconsin - Madison, Computer Sciences
* Dept. and Dept. of Electrical and Computer Engineering, as a part of
* Prof. Mikko Lipasti's Fall 1999 ECE 902 course.
*
* Copyright (C) 1999, 2000 by Harold Cain, Timothy Heil, Milo Martin, 
*                             Eric Weglarz, Todd Bezenek.
*
* This source code is distributed "as is" in the hope that it will be
* useful.  It comes with no warranty, and no author or distributor
* accepts any responsibility for the consequences of its use.
*
* Everyone is granted permission to copy, modify and redistribute
* this code under the following conditions:
*
* This code is distributed for non-commercial use only.
* Please contact the maintainer for restrictions applying to 
* commercial use of these tools.
*
* Permission is granted to anyone to make or distribute copies
* of this code, either as received or modified, in any
* medium, provided that all copyright notices, permission and
* nonwarranty notices are preserved, and that the distributor
* grants the recipient permission for further redistribution as
* permitted by this document.
*
* Permission is granted to distribute this code in compiled
* or executable form under the same conditions that apply for
* source code, provided that either:
*
* A. it is accompanied by the corresponding machine-readable
*    source code,
* B. it is accompanied by a written offer, with no time limit,
*    to give anyone a machine-readable copy of the corresponding
*    source code in return for reimbursement of the cost of
*    distribution.  This written offer must permit verbatim
*    duplication by anyone, or
* C. it is distributed by someone who received only the
*    executable form, and is accompanied by a copy of the
*    written offer of source code that they received concurrently.
*
* In other words, you are welcome to use, share and improve this codes.
* You are forbidden to forbid anyone else to use, share and improve what
* you give them.
*
************************************************************************/

//DATABASE CONNECTIVITY NEEDED: Lots!
//1. Given a SHOPPING_ID, I need a way to get the SCL_ID, SC_COST, and 
//   SC_QTY for each item in the shopping cart, as well as the SC_SUBTOTAL,
//   SC_TAX, SC_SHIP_COST, and SC_TOTAL for the entire cart.
//
//2. Given a C_ID, I need a DB call that returns the C_FNAME, C_LNAME,
//   C_DISCOUNT, and C_ADDR_ID from the customer table. 
//
//3. I need a function which takes, as parameters, STREET_1, STREET_2, CITY,
//   STATE, ZIP, and COUNTRY. The DB code should search the ADDRESS table for
//   and address which matches these parameters. If one does not exist,
//   a new row is added to the ADDRESS table with these parameters as columns.
//   The lookup and the insertion must happen in a single DB transaction
//   This is described in section 2.7.3.2


//4. I also need a DB function which does what is described in section 
//   2.7.3.3, given the following parameters: C_ID, SC_SUB_TOTAL, SC_TOTAL,
//   SHIPPING, C_ADDR_ID, and ADDR_ID. This involves putting the order in
//   The ORDER DB table, creating a message to the SSL PGE (which is currently
//   never sent), putting an entry in the CC table, and then clearing all of
//   the items from the cart. This function should return the newly created
//   unique O_ID.
//   

public class TPCW_buy_confirm_servlet extends LoadBalanceHttpServlet {

}
